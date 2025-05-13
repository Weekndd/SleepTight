import 'package:app/features/sleep_mode/data/models/enums/sleep_stage.dart';

class SleepStage {
  final SleepStageType stageType;
  final String startTime;
  final String endTime;

  SleepStage({
    required this.stageType,
    required this.startTime,
    required this.endTime,
  });

  Map<String, dynamic> toJson() {
    return {
      'stageType': stageType.value,
      'startTime': startTime,
      'endTime': endTime,
    };
  }
}

class SleepEndRequest {
  final int reportId;
  final String sleepEndTime;
  final List<SleepStage> stages;

  SleepEndRequest({
    required this.reportId,
    required this.sleepEndTime,
    required this.stages,
  });

  Map<String, dynamic> toJson() {
    return {
      'reportId': reportId,
      'sleepEndTime': sleepEndTime,
      'stages': stages.map((stage) => stage.toJson()).toList(),
    };
  }
}
