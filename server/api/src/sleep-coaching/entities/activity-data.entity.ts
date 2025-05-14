import {
  Entity,
  Column,
  PrimaryGeneratedColumn,
  CreateDateColumn,
  Index,
} from 'typeorm';
import { ActivityDataType } from './activity-data.enum';
import { ActivityUnit } from './activity-unit.enum';
ActivityUnit;

@Entity('activity_data')
@Index(['userId', 'dataType', 'activityStartTime'])
export class ActivityData {
  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  userId: number;

  @Column({
    type: 'uuid',
    nullable: true,
  })
  uuid: string;

  @Column({
    type: 'enum',
    enum: ActivityDataType,
  })
  dataType: ActivityDataType;

  // 수치 데이터
  @Column({
    type: 'float',
    nullable: true,
  })
  valueNumber: number;

  // 복합 데이터
  @Column({
    type: 'jsonb',
    nullable: true,
  })
  valueJson: Record<string, any>;

  @Column({
    type: 'enum',
    enum: ActivityUnit,
    nullable: true,
  })
  unit: ActivityUnit;

  @Column({ type: 'timestamptz' })
  activityStartTime: Date;

  @Column({ type: 'timestamptz' })
  activityEndTime: Date;

  @CreateDateColumn()
  createdAt: Date;
}
