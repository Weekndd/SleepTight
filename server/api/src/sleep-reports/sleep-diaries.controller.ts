import {
  Controller,
  Post,
  Get,
  Patch,
  Body,
  Param,
  Req,
  UseGuards,
  BadRequestException,
} from '@nestjs/common';
import { JwtAuthGuard } from 'src/auth/jwt-auth.guard';
import { SleepDiariesService } from './sleep-diaries.service';
import { UpdateSleepDiaryDto } from './dto/update-sleep-diary.request.dto';
import { SleepDiaryResponseDto } from './dto/sleep-diary.response.dto';
import { ApiBearerAuth } from '@nestjs/swagger';

@Controller('sleep-reports/diaries') // ← 전역 /api prefix를 쓰므로 여기서는 api/ 생략
@UseGuards(JwtAuthGuard)
@ApiBearerAuth()
export class SleepDiariesController {
  constructor(private readonly diariesService: SleepDiariesService) {}

  /** 특정 일자 일지 목록 조회 - 구체적인 경로가 먼저 오도록 순서 변경 */
  @Get('date/:date')
  async findByDate(
    @Req() req,
    @Param('date') date: string,
  ): Promise<(SleepDiaryResponseDto | null)[]> {
    // 날짜 형식 검증
    if (!/^\d{4}-\d{2}-\d{2}$/.test(date)) {
      throw new BadRequestException('date는 YYYY-MM-DD 형식이어야 합니다.');
    }

    const userId = req.user.userId;
    const diaries = await this.diariesService.findByDate(userId, date);

    // 기존 응답 형식 유지
    return diaries;
  }

  /** 특정 리포트 일지 조회 */
  @Get(':reportId')
  async findByReportId(
    @Req() req,
    @Param('reportId') reportId: string,
  ): Promise<SleepDiaryResponseDto> {
    const userId = req.user.userId;
    return this.diariesService.findByReportId(userId, +reportId);
  }

  /** 일지 수정 */
  @Patch()
  update(@Req() req, @Body() dto: UpdateSleepDiaryDto) {
    const userId = req.user.userId;
    return this.diariesService.update(userId, dto);
  }
}
