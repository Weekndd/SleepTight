import { Module } from '@nestjs/common';
import { SoundController } from './sound.controller';
import { SoundService } from './sound.service';
import { SleepSound } from './entities/sleep-sound.entity';
import { S3Module } from 'src/common/aws/s3.module';
import { TypeOrmModule } from '@nestjs/typeorm';

@Module({
  imports: [TypeOrmModule.forFeature([SleepSound]), S3Module],
  controllers: [SoundController],
  providers: [SoundService],
})
export class SoundModule {}
