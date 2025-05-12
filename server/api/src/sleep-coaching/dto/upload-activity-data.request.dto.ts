import {
  IsArray,
  IsEnum,
  IsNumber,
  IsOptional,
  IsDateString,
  ValidateNested,
} from 'class-validator';
import { Type } from 'class-transformer';
import { ActivityDataType } from '../entities/activity-data.enum';
import { ActivityUnit } from '../entities/activity-unit.enum';

export class ActivityRecordDto {
  @IsEnum(ActivityDataType)
  dataType: ActivityDataType;

  @IsNumber()
  value: number | Record<string, any>;

  @IsOptional()
  @IsEnum(ActivityUnit)
  unit?: ActivityUnit;

  @IsDateString()
  startTime: string;

  @IsDateString()
  endTime: string;
}

export class UploadActivityDataRequestDto {
  @IsArray()
  @ValidateNested({ each: true })
  @Type(() => ActivityRecordDto)
  records: ActivityRecordDto[];
}
