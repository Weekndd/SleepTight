import 'package:app/features/health/services/health_service.dart';
import 'package:health/health.dart';

Future<void> appendSleepDataToBuffer({
  required StringBuffer sb,
  required DateTime startDate,
  required DateTime endDate,
}) async {
  try {
    List<HealthDataPoint> sleepData = await HealthService.fetchSleepData(
      startDate,
      endDate,
    );

    if (sleepData.isEmpty) {
      sb.writeln("지난 24시간 동안의 수면 데이터가 없습니다.");
      return;
    }

    // CSV 헤더 추가
    sb.writeln(
      "UUID,Value,Unit,Type,SourcePlatform,DateFrom,DateTo,SourceName",
    );

    for (HealthDataPoint p in sleepData) {
      String valueStr = "N/A";
      String unitDisplayStr = p.unitString;

      if (p.value is NumericHealthValue) {
        var numVal = (p.value as NumericHealthValue).numericValue;
        valueStr = numVal.toString();
      } else {
        valueStr = p.value.toString();
      }

      sb.writeln(
        "${p.uuid},"
        "$valueStr,"
        "$unitDisplayStr,"
        "${p.typeString},"
        "${p.sourcePlatform.name},"
        "${p.dateFrom.toIso8601String()},"
        "${p.dateTo.toIso8601String()},"
        "${p.sourceName}",
      );
    }
  } catch (e) {
    sb.writeln("수면 데이터 조회 중 오류: $e");
  }
}
