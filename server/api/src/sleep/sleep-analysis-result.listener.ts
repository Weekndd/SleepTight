import { Controller, Inject } from "@nestjs/common";
import { Ctx, EventPattern, Payload, RmqContext } from "@nestjs/microservices";
import { AnalysisResultDto } from "./dto/analysis-result.dto";
import { SleepSoundService } from "./sleep-sound.service";
import { ConfigService } from "@nestjs/config";

@Controller()
export class SleepAnalysisResultListener {
    constructor(
        private readonly sleepSoundService :SleepSoundService,
    ){}
    @EventPattern('analysis.result') //라우팅키
    async handleAnalysisResult (@Payload() data :AnalysisResultDto,
    ) {
        console.log('🐰 분석 결과 수신:', data);
        try {
            await this.sleepSoundService.saveSleepEvent(data);
            console.log('Saved sleep event:', data);
        } catch (e) {
            console.error('Failed to save sleep event:', e);
        }
        
    }
}