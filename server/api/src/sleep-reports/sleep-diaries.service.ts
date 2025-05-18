import {
  BadRequestException,
  Injectable,
  NotFoundException,
  ConflictException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository, In } from 'typeorm';
import { SleepDiary } from './entities/sleep-diary.entity';
import { SleepReport } from './entities/sleep-report.entity';
import { CreateSleepDiaryDto } from './dto/create-sleep-diary.dto';
import { UpdateSleepDiaryDto } from './dto/update-sleep-diary.dto';
import { SleepDiaryResponseDto } from './dto/sleep-diary.response.dto';

@Injectable()
export class SleepDiariesService {
  constructor(
    @InjectRepository(SleepDiary)
    private readonly diaryRepo: Repository<SleepDiary>,
    @InjectRepository(SleepReport)
    private readonly reportRepo: Repository<SleepReport>,
  ) {}

  /** 해당 리포트에 이미 일지가 있으면 중복 에러 */
  private async ensureNotExists(reportId: number) {
    const exist = await this.diaryRepo.findOne({
      where: { sleepReportId: reportId },
    });
    if (exist) {
      throw new ConflictException(
        `Diary for report ${reportId} already exists`,
      );
    }
  }

  /** 일지 생성 */
  async create(userId: number, dto: CreateSleepDiaryDto): Promise<SleepDiary> {
    // 1) 클라이언트가 넘겨준 reportId로 소유 여부 확인
    const report = await this.reportRepo.findOne({
      where: { id: dto.sleepReportId, userId },
    });
    if (!report) {
      throw new NotFoundException(`SleepReport ${dto.sleepReportId} not found`);
    }

    // 2) 날짜 중복 체크
    await this.ensureNotExists(dto.sleepReportId);

    // 3) 일지 생성
    const { sleepReportId, ...rest } = dto;
    const diary = this.diaryRepo.create({ sleepReportId, ...rest });
    return this.diaryRepo.save(diary);
  }

  /** 리포트 ID로 일지 조회 */
  async findByReportId(
    userId: number,
    reportId: number,
  ): Promise<SleepDiaryResponseDto> {
    const report = await this.reportRepo.findOne({
      where: { id: reportId, userId },
    });
    if (!report) {
      throw new NotFoundException(`No SleepReport for reportId ${reportId}`);
    }

    const diary = await this.diaryRepo.findOne({
      where: { sleepReportId: reportId },
    });
    if (!diary) {
      throw new NotFoundException(`Diary not found for reportId ${reportId}`);
    }
    return this.toResponseDto(diary);
  }

  /**
   * 날짜별 다이어리 조회(리포트 기준준)
   *  날짜로 보고서 목록을 먼저 조회한 뒤,
   *  각 보고서별 일지가 있으면 가져오고, 없으면 null 로 채워 반환
   *  @param userId
   *  @param date  YYYY-MM-DD 형식의 문자열
   */
  async findByDate(
    userId: number,
    date: string,
  ): Promise<(SleepDiaryResponseDto | null)[]> {
    // 1) date 포맷 검증
    if (!/^\d{4}-\d{2}-\d{2}$/.test(date)) {
      throw new BadRequestException('date는 YYYY-MM-DD 형식이어야 합니다.');
    }

    // 2) QueryBuilder 로 date 문자열 직접 바인딩
    const reports = await this.reportRepo
      .createQueryBuilder('report')
      .select('report.id')
      .where('report.user_id = :userId', { userId })
      .andWhere('report.is_valid_report = :isValid', { isValid: true })
      .andWhere('report.sleep_date = :date', { date })
      .orderBy('report.sleep_end_time', 'DESC')
      .getMany();

    if (reports.length === 0) {
      return [];
    }

    // 3) diary 조회 및 매핑
    const reportIds = reports.map((r) => r.id);
    const diaries = await this.diaryRepo.find({
      where: { sleepReportId: In(reportIds) },
      order: { id: 'DESC' },
    });
    const diaryMap = new Map<number, SleepDiary>();
    for (const d of diaries) {
      if (!diaryMap.has(d.sleepReportId)) diaryMap.set(d.sleepReportId, d);
    }

    // 4) 순서대로 DTO 나열 (없으면 null)
    return reports.map((r) => {
      const ent = diaryMap.get(r.id);
      return ent ? this.toResponseDto(ent) : null;
    });
  }

  /** 일지 수정 */
  async update(userId: number, dto: UpdateSleepDiaryDto): Promise<SleepDiary> {
    // 1) report 확인
    const report = await this.reportRepo.findOne({
      where: { id: dto.sleepReportId, userId },
    });
    if (!report) {
      throw new NotFoundException(`No SleepReport for id ${dto.sleepReportId}`);
    }

    // 2) 일지 존재 확인
    const diary = await this.diaryRepo.findOne({
      where: { sleepReportId: dto.sleepReportId },
    });
    if (!diary) {
      throw new NotFoundException(
        `Diary not found for reportId ${dto.sleepReportId}`,
      );
    }

    // 3) 수정할 필드 덮어쓰기
    Object.assign(diary, dto);
    return this.diaryRepo.save(diary);
  }

  /** 엔티티 → DTO 변환 헬퍼 */
  private toResponseDto(entity: SleepDiary): SleepDiaryResponseDto {
    const dto = new SleepDiaryResponseDto();
    dto.id = entity.id;
    dto.sleepReportId = entity.sleepReportId;
    dto.sleepDate = entity.sleepDate;
    dto.sleepTime = entity.sleepTime;
    dto.wakeTime = entity.wakeTime;
    dto.sleepLatency = entity.sleepLatency;
    dto.wakeCount = entity.wakeCount;
    dto.sleepQuality = entity.sleepQuality;
    dto.moodScore = entity.moodScore;
    dto.wakeAwareness = entity.wakeAwareness;
    dto.wakeMethod = entity.wakeMethod;
    dto.wakeMethodEtc = entity.wakeMethodEtc;
    return dto;
  }
}
