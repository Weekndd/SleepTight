import {
  Column,
  Entity,
  PrimaryGeneratedColumn,
  CreateDateColumn,
  Index,
  PrimaryColumn,
} from 'typeorm';

@Entity('sleep_sounds')
export class SleepSound {
  @PrimaryColumn({ type: 'uuid' })
  @Index({ unique: true })
  segmentId: string;

  @Column({ name: 'sleep_report_id', type: 'int' })
  sleepReportId: number;

  @Column({ name: 'voice_url', type: 'varchar', length: 255 })
  voiceUrl: string;

  @Column({ name: 'has_anomaly', type: 'boolean', default: false })
  hasAnomaly: boolean;

  @Column({ name: 'inference_completed', type: 'boolean', default: false })
  inferenceCompleted: boolean;

  @CreateDateColumn({ name: 'created_at', type: 'timestamp with time zone' })
  createdAt: Date;

  @Column({ name: 'duration', type: 'float' })
  duration: number;
}
