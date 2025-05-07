import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:shared_preferences/shared_preferences.dart';

part 'alarm_provider.g.dart';

class AlarmTime {
  final String amPm;
  final int hour;
  final int minute;
  final bool isAlarmOn;

  AlarmTime({
    required this.amPm,
    required this.hour,
    required this.minute,
    required this.isAlarmOn,
  });

  AlarmTime copyWith({String? amPm, int? hour, int? minute, bool? isAlarmOn}) {
    return AlarmTime(
      amPm: amPm ?? this.amPm,
      hour: hour ?? this.hour,
      minute: minute ?? this.minute,
      isAlarmOn: isAlarmOn ?? this.isAlarmOn,
    );
  }

  static Future<AlarmTime> loadFromPreferences() async {
    final prefs = await SharedPreferences.getInstance();
    final amPm = prefs.getString('amPm') ?? '오전';
    final hour = prefs.getInt('hour') ?? 7;
    final minute = prefs.getInt('minute') ?? 0;
    final isAlarmOn = prefs.getBool('isAlarmOn') ?? false;

    return AlarmTime(
      amPm: amPm,
      hour: hour,
      minute: minute,
      isAlarmOn: isAlarmOn,
    );
  }

  Future<void> saveToPreferences() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('amPm', amPm);
    await prefs.setInt('hour', hour);
    await prefs.setInt('minute', minute);
    await prefs.setBool('isAlarmOn', isAlarmOn);
  }
}

@riverpod
Future<AlarmTime> alarmTime(Ref ref) async {
  final alarmTime = await AlarmTime.loadFromPreferences();
  return alarmTime;
}

@riverpod
FutureOr<void> toggleAlarmStatus(Ref ref) async {
  final current = await ref.read(alarmTimeProvider.future);
  final updated = current.copyWith(isAlarmOn: !current.isAlarmOn);
  await updated.saveToPreferences();

  ref.invalidate(alarmTimeProvider);
}

@riverpod
FutureOr<void> updateAlarmTime(Ref ref, AlarmTime newTime) async {
  await newTime.saveToPreferences();
  ref.invalidate(alarmTimeProvider);
}
