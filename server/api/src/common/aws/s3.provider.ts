import { S3Client } from '@aws-sdk/client-s3';
import { ConfigService } from '@nestjs/config';

export const S3Provider = [
  {
    provide: 'S3_CLIENT',
    inject: [ConfigService],
    useFactory: (configService: ConfigService) => {
      return new S3Client({
        region: configService.get('AWS_S3_REGION'),
        credentials: {
          accessKeyId: configService.get('AWS_ACCESS_KEY'),
          secretAccessKey: configService.get('AWS_SECRET_KEY'),
        },
      });
    },
  },
];
