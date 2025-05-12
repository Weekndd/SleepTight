enum SleepStageType {
  awake('AWAKE'),
  light('LIGHT'),
  deep('DEEP'),
  rem('REM');

  final String value;
  const SleepStageType(this.value);

  // 문자열 → SleepStageType 변환
  static SleepStageType fromString(String? value) {
    return SleepStageType.values.firstWhere(
      (e) => e.value == value,
      orElse: () => SleepStageType.awake,
    );
  }
}
