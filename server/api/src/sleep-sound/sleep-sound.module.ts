import { Module } from '@nestjs/common';
import { SleepSoundController } from './sleep-sound.controller';
import { SleepSoundService } from './sleep-sound.service';
import { SleepSound } from './entities/sleep-sound.entity';
import { S3Module } from 'src/common/aws/s3.module';
import { TypeOrmModule } from '@nestjs/typeorm';
import { SleepSoundFactory } from './sleep-sound.factory';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { SleepSoundProducer } from './sleep-sound.producer';
import { ClientsModule, Transport } from '@nestjs/microservices';
import { SleepStageService } from '../sleep-reports/sleep-stage.service';
import { SleepStageFactory } from '../sleep-reports/sleep-stage.factory';
import { SleepStageLog } from '../sleep-reports/entities/sleep-stage-log.entity';

@Module({
  imports: [
    TypeOrmModule.forFeature([SleepSound, SleepStageLog]),
    S3Module,
    ConfigModule,
    ClientsModule.registerAsync([
      {
        name: 'RABBITMQ_SERVICE',
        imports: [ConfigModule],
        inject: [ConfigService],
        useFactory: (config: ConfigService) => ({
          transport: Transport.RMQ,
          options: {
            urls: [
              `amqp://${config.get('RABBITMQ_DEFAULT_USER')}:${config.get('RABBITMQ_DEFAULT_PASS')}@${config.get('RABBITMQ_HOST')}:${config.get('RABBITMQ_PORT')}`,
            ],
            queue: 'sleep.metadata.queue',
            exchange: 'sleep.exchange',
            queueOptions: { durable: false },
          },
        }),
      },
    ]),
  ],
  controllers: [SleepSoundController],
  providers: [SleepSoundProducer, SleepSoundService, SleepSoundFactory],
  exports: [SleepSoundService],
})
export class SleepSoundModule {}
