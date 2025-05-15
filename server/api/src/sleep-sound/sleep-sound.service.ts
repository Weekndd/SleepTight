import { ExceptionCode } from 'src/common/exceptions/exception-code.enum';
import { ConfigService } from '@nestjs/config';
import { SleepSoundFactory } from './sleep-sound.factory';
import {
  ConflictException,
  forwardRef,
  Inject,
  Injectable,
} from '@nestjs/common';
import { UploadSleepSoundRequestDto } from './dto/upload-sleep-sound.request.dto';
import { UploadSleepSoundResponseDto } from './dto/upload-sleep-sound.response.dto';
import { S3Client, PutObjectCommand } from '@aws-sdk/client-s3';
import { SleepSoundProducer } from './sleep-sound.producer';
import { throwBadRequest } from 'src/common/exceptions/exception.helper';
import { AnalysisResultDto } from './dto/analysis-result.dto';
import { SleepEvent } from './entities/sleep-event.entity';
import { AnomalyType } from './entities/anomaly.enum';

@Injectable()
export class SleepSoundService {
  constructor(
    @Inject('S3_CLIENT') private readonly s3: S3Client,
    private readonly configService: ConfigService,
    private readonly sleepSoundFactory: SleepSoundFactory,
    @Inject(forwardRef(() => SleepSoundProducer))
    private readonly sleepSoundProducer: SleepSoundProducer,
  ) {}

  async handleUpload(
    file: Express.Multer.File,
    body: UploadSleepSoundRequestDto,
  ): Promise<UploadSleepSoundResponseDto> {
    const { segmentId, reportId, duration, timestamp } = body;

    const exists = await this.sleepSoundFactory.exist({
      where: { segmentId },
    });
    if (exists) {
      throwBadRequest(ExceptionCode.DUPLICATE_SEGMENT_ID);
    }

    const key = `audio-prod/${segmentId}.opus`;
    const bucket = this.configService.get<string>('AWS_S3_BUCKET')!;
    const region = this.configService.get<string>('AWS_S3_REGION')!;

    // S3에 파일 업로드
    await this.s3.send(
      new PutObjectCommand({
        Bucket: bucket,
        Key: key,
        Body: file.buffer,
        ContentType: file.mimetype,
      }),
    );

    const fileUrl = `https://${bucket}.s3.${region}.amazonaws.com/${key}`;

    // DB에 음성 메타데이터 저장
    const sleepSound = this.sleepSoundFactory.create({
      reportId,
      segmentId,
      fileUrl,
      duration,
    });
    await this.sleepSoundFactory.save(sleepSound);

    // RabbitMQ에 메타데이터 발행 요청
    await this.sleepSoundProducer.publishSegmentMetadata({
      segmentId,
      s3Key: key,
      timestamp,
      duration,
      codec: 'opus',
    });
    return UploadSleepSoundResponseDto.from({ segmentId, fileUrl });
  }

  async saveSleepEvent(dto: AnalysisResultDto) {
    const sleepEvent: SleepEvent = AnalysisResultDto.toEntity(dto);
    this.sleepSoundFactory.saveSleepEvent(sleepEvent);
  }

  async calculateEventDurations(reportId: number): Promise<{
    snoring: number;
    somniloquy: number;
    coughing: number;
  }> {
    // 해당 리포트에 연결된 segmentId들 조회
    const sounds = await this.sleepSoundFactory.findByReportId(reportId);
    const segmentIds = sounds.map((sound) => sound.segmentId);
    if (!segmentIds.length) return { snoring: 0, somniloquy: 0, coughing: 0 };

    // segmentId에 해당하는 이벤트들 조회
    const events =
      await this.sleepSoundFactory.findEventsBySegmentIds(segmentIds);

    // anomaly 기준으로 누적 시간 계산
    let snoring = 0,
      somniloquy = 0,
      coughing = 0;

    for (const event of events) {
      const duration = event.endSec - event.startSec;
      switch (event.anomaly) {
        case AnomalyType.SNORE:
          snoring += duration;
          break;
        case AnomalyType.SOMNILOQUY:
          somniloquy += duration;
          break;
        case AnomalyType.COUGH:
          coughing += duration;
          break;
      }
    }

    return {
      snoring: Math.round(snoring),
      somniloquy: Math.round(somniloquy),
      coughing: Math.round(coughing),
    };
  }
}
