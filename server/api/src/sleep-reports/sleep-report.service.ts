import { SleepReportFactory } from './sleep-report.factory';
import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository, DataSource } from 'typeorm';
import { SleepReport } from './entities/sleep-report.entity';
import { SleepStageService } from 'src/sleep-reports/sleep-stage.service';
import { UploadSleepStagesDto } from 'src/sleep-reports/dto/upload-sleep-stage.request.dto';
import { StartSleepRequestDto } from './dto/start-sleep.request.dto';
import { User } from 'src/users/entities/user.entity';
import { throwNotFoundException } from 'src/common/exceptions/exception.helper';
import { ExceptionCode } from 'src/common/exceptions/exception-code.enum';

@Injectable()
export class SleepReportService {
  constructor(
    private readonly dataSource: DataSource,
    @InjectRepository(SleepReport)
    private readonly reportRepo: Repository<SleepReport>,
    @InjectRepository(User)
    private readonly userRepo: Repository<User>,
    private readonly sleepStageService: SleepStageService,
    private readonly reportFactory: SleepReportFactory,
  ) {}

  // 수면 시작
  async startSleep(userId: number, dto: StartSleepRequestDto): Promise<number> {
    const sleepStartTime = new Date(dto.sleep_start_time);

    const user = await this.userRepo.findOneBy({ id: userId });
    if (!user) {
      throwNotFoundException(ExceptionCode.USER_NOT_FOUND);
    }

    // 목표 기상 시간으로 기준 일자 판단
    const wakeHour = parseInt(user.wake_time.split(':')[0], 10);
    const sleepDate = new Date(sleepStartTime);
    if (sleepStartTime.getHours() < wakeHour) {
      sleepDate.setDate(sleepDate.getDate() - 1);
    }
    const sleepDateOnly = new Date(sleepDate.toDateString());

    // 기존 유효하지 않은 리포트가 있다면 재사용
    const existing = await this.reportRepo.findOne({
      where: {
        userId,
        isValidReport: false,
      },
      order: { createdAt: 'DESC' },
    });

    if (existing) {
      existing.sleepStartTime = sleepStartTime;
      existing.isValidReport = true;
      return (await this.reportRepo.save(existing)).id;
    }

    // 없으면 리포트 새로 생성
    const newReport = this.reportFactory.createNew(
      userId,
      sleepStartTime,
      sleepDateOnly,
    );

    // 저장 후 리포트 ID 반환
    const saved = await this.reportFactory.save(newReport);
    return saved.id;
  }

  // 수면 종료 + 수면 단계 업로드
  async endSleep(dto: UploadSleepStagesDto): Promise<void> {
    const report = await this.reportRepo.findOneByOrFail({ id: dto.reportId });
    if (!report) {
      throwNotFoundException(ExceptionCode.REPORT_NOT_FOUND);
    }
    await this.dataSource.transaction(async (manager) => {
      // 종료 시간 계산
      const sleepEndTime = new Date(dto.sleepEndTime);
      report.sleepEndTime = sleepEndTime;

      const durationMs =
        sleepEndTime.getTime() - report.sleepStartTime.getTime();
      const isValidSleep = durationMs >= 60 * 60 * 1000;
      report.isValidReport = isValidSleep;

      if (isValidSleep) {
        report.sleepDate = new Date(sleepEndTime.toDateString());

        await this.sleepStageService.saveStages(dto.stages, report.id, manager);
      }
      await manager.save(report);
    });
  }
}
