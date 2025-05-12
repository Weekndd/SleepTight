import { ActivityDataType } from './entities/activity-data.enum';
import { ActivityRecordDto } from './dto/upload-activity-data.request.dto';

export class ActivityDataService {
  constructor(private readonly activityRepo: any) {}

  async saveActivityRecords(
    userId: number,
    records: ActivityRecordDto[],
  ): Promise<void> {
    for (const record of records) {
      const entity = this.activityRepo.create({
        userId,
        dataType: record.dataType,
        unit: record.unit,
        activityStartTime: new Date(record.startTime),
        activityEndTime: new Date(record.endTime),
      });

      if (
        record.dataType === ActivityDataType.WORKOUT ||
        record.dataType === ActivityDataType.NUTRITION ||
        record.dataType === ActivityDataType.MENSTRUATION_FLOW
      ) {
        // 복합(json) 형태
        entity.valueJson = record.value;
      } else {
        // 숫자형 데이터
        entity.valueNumber = Number(record.value);
      }

      await this.activityRepo.save(entity);
    }
  }
}
