// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'alarm_provider.dart';

// **************************************************************************
// RiverpodGenerator
// **************************************************************************

String _$alarmTimeHash() => r'af0c8bcc5f84bb2d02f0e8a668573e2e19aa6485';

/// See also [alarmTime].
@ProviderFor(alarmTime)
final alarmTimeProvider = AutoDisposeFutureProvider<AlarmTime>.internal(
  alarmTime,
  name: r'alarmTimeProvider',
  debugGetCreateSourceHash:
      const bool.fromEnvironment('dart.vm.product') ? null : _$alarmTimeHash,
  dependencies: null,
  allTransitiveDependencies: null,
);

@Deprecated('Will be removed in 3.0. Use Ref instead')
// ignore: unused_element
typedef AlarmTimeRef = AutoDisposeFutureProviderRef<AlarmTime>;
String _$toggleAlarmStatusHash() => r'56339af2dce9bd4d68c3f1bb3e80609f9ff0fb95';

/// See also [toggleAlarmStatus].
@ProviderFor(toggleAlarmStatus)
final toggleAlarmStatusProvider = AutoDisposeFutureProvider<void>.internal(
  toggleAlarmStatus,
  name: r'toggleAlarmStatusProvider',
  debugGetCreateSourceHash:
      const bool.fromEnvironment('dart.vm.product')
          ? null
          : _$toggleAlarmStatusHash,
  dependencies: null,
  allTransitiveDependencies: null,
);

@Deprecated('Will be removed in 3.0. Use Ref instead')
// ignore: unused_element
typedef ToggleAlarmStatusRef = AutoDisposeFutureProviderRef<void>;
String _$updateAlarmTimeHash() => r'fbe1e99db84edf8a46b9c5f0776e6ae6840b270e';

/// Copied from Dart SDK
class _SystemHash {
  _SystemHash._();

  static int combine(int hash, int value) {
    // ignore: parameter_assignments
    hash = 0x1fffffff & (hash + value);
    // ignore: parameter_assignments
    hash = 0x1fffffff & (hash + ((0x0007ffff & hash) << 10));
    return hash ^ (hash >> 6);
  }

  static int finish(int hash) {
    // ignore: parameter_assignments
    hash = 0x1fffffff & (hash + ((0x03ffffff & hash) << 3));
    // ignore: parameter_assignments
    hash = hash ^ (hash >> 11);
    return 0x1fffffff & (hash + ((0x00003fff & hash) << 15));
  }
}

/// See also [updateAlarmTime].
@ProviderFor(updateAlarmTime)
const updateAlarmTimeProvider = UpdateAlarmTimeFamily();

/// See also [updateAlarmTime].
class UpdateAlarmTimeFamily extends Family<AsyncValue<void>> {
  /// See also [updateAlarmTime].
  const UpdateAlarmTimeFamily();

  /// See also [updateAlarmTime].
  UpdateAlarmTimeProvider call(AlarmTime newTime) {
    return UpdateAlarmTimeProvider(newTime);
  }

  @override
  UpdateAlarmTimeProvider getProviderOverride(
    covariant UpdateAlarmTimeProvider provider,
  ) {
    return call(provider.newTime);
  }

  static const Iterable<ProviderOrFamily>? _dependencies = null;

  @override
  Iterable<ProviderOrFamily>? get dependencies => _dependencies;

  static const Iterable<ProviderOrFamily>? _allTransitiveDependencies = null;

  @override
  Iterable<ProviderOrFamily>? get allTransitiveDependencies =>
      _allTransitiveDependencies;

  @override
  String? get name => r'updateAlarmTimeProvider';
}

/// See also [updateAlarmTime].
class UpdateAlarmTimeProvider extends AutoDisposeFutureProvider<void> {
  /// See also [updateAlarmTime].
  UpdateAlarmTimeProvider(AlarmTime newTime)
    : this._internal(
        (ref) => updateAlarmTime(ref as UpdateAlarmTimeRef, newTime),
        from: updateAlarmTimeProvider,
        name: r'updateAlarmTimeProvider',
        debugGetCreateSourceHash:
            const bool.fromEnvironment('dart.vm.product')
                ? null
                : _$updateAlarmTimeHash,
        dependencies: UpdateAlarmTimeFamily._dependencies,
        allTransitiveDependencies:
            UpdateAlarmTimeFamily._allTransitiveDependencies,
        newTime: newTime,
      );

  UpdateAlarmTimeProvider._internal(
    super._createNotifier, {
    required super.name,
    required super.dependencies,
    required super.allTransitiveDependencies,
    required super.debugGetCreateSourceHash,
    required super.from,
    required this.newTime,
  }) : super.internal();

  final AlarmTime newTime;

  @override
  Override overrideWith(
    FutureOr<void> Function(UpdateAlarmTimeRef provider) create,
  ) {
    return ProviderOverride(
      origin: this,
      override: UpdateAlarmTimeProvider._internal(
        (ref) => create(ref as UpdateAlarmTimeRef),
        from: from,
        name: null,
        dependencies: null,
        allTransitiveDependencies: null,
        debugGetCreateSourceHash: null,
        newTime: newTime,
      ),
    );
  }

  @override
  AutoDisposeFutureProviderElement<void> createElement() {
    return _UpdateAlarmTimeProviderElement(this);
  }

  @override
  bool operator ==(Object other) {
    return other is UpdateAlarmTimeProvider && other.newTime == newTime;
  }

  @override
  int get hashCode {
    var hash = _SystemHash.combine(0, runtimeType.hashCode);
    hash = _SystemHash.combine(hash, newTime.hashCode);

    return _SystemHash.finish(hash);
  }
}

@Deprecated('Will be removed in 3.0. Use Ref instead')
// ignore: unused_element
mixin UpdateAlarmTimeRef on AutoDisposeFutureProviderRef<void> {
  /// The parameter `newTime` of this provider.
  AlarmTime get newTime;
}

class _UpdateAlarmTimeProviderElement
    extends AutoDisposeFutureProviderElement<void>
    with UpdateAlarmTimeRef {
  _UpdateAlarmTimeProviderElement(super.provider);

  @override
  AlarmTime get newTime => (origin as UpdateAlarmTimeProvider).newTime;
}

// ignore_for_file: type=lint
// ignore_for_file: subtype_of_sealed_class, invalid_use_of_internal_member, invalid_use_of_visible_for_testing_member, deprecated_member_use_from_same_package
