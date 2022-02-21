import 'dart:async';
import 'dart:io';
import 'dart:ui';

import 'package:flutter/services.dart';
import 'package:laudyou_app_service/utils/commons.dart';

import 'utils/constants.dart';

class IosConfiguration {
  /// 이 메소드는 앱이 포그라운드에 있을 때 실행됩니다.
  final Function onForeground;

  /// must be a top level or static method
  /// this method will be executed by background fetch
  /// make sure you don't execute long running task there because of limitations on ios
  /// recommended maximum executed duration is only 15-20 seconds.
  final Function onBackground;

  /// wheter service auto start after configure.
  final bool autoStart;

  IosConfiguration({
    required this.onForeground,
    required this.onBackground,
    this.autoStart = true,
  });
}

class AndroidConfiguration {
  /// must be a top level or static method
  final Function onStart;

  /// wheter service can started automatically on boot and after configure
  final bool autoStart;

  /// wheter service is foreground or background mode
  final bool isForegroundMode;

  final String? foregroundServiceNotificationTitle;
  final String? foregroundServiceNotificationContent;

  AndroidConfiguration({
    required this.onStart,
    this.autoStart = true,
    required this.isForegroundMode,
    this.foregroundServiceNotificationContent,
    this.foregroundServiceNotificationTitle,
  });
}

enum SystemWindowPrefMode { DEFAULT, OVERLAY, BUBBLE }

class LaudyouAppService {
  bool _isFromInitialization = false;
  bool _isRunning = false;
  bool _isMainChannel = false;

  static const MethodChannel _backgroundChannel =
      MethodChannel(Constants.BACKGROUND_CHANNEL, JSONMethodCodec());

  static const MethodChannel _mainChannel =
      MethodChannel(Constants.CHANNEL, JSONMethodCodec());

  static final LaudyouAppService _instance = LaudyouAppService._internal()
    .._setupBackground();

  LaudyouAppService._internal();
  factory LaudyouAppService() => _instance;

  void _setupMain() {
    _isFromInitialization = true;
    _isRunning = true;
    _isMainChannel = true;
    _mainChannel.setMethodCallHandler(_handle);
  }

  void _setupBackground() {
    _isRunning = true;
    _backgroundChannel.setMethodCallHandler(_handle);
  }

  Future<dynamic> _handle(MethodCall call) async {
    switch (call.method) {
      case "onReceiveData":
        _streamController.sink.add(call.arguments);
        break;
      default:
    }

    return true;
  }

  Future<bool> start() async {
    if (!_isMainChannel) {
      throw Exception(
          'This method only allowed from UI. Please call configure() first.');
    }

    final result = await _mainChannel.invokeMethod('start');
    return result ?? false;
  }

  Future<bool> configure({
    required IosConfiguration iosConfiguration,
    required AndroidConfiguration androidConfiguration,
  }) async {
    if (Platform.isAndroid) {
      final CallbackHandle? handle =
          PluginUtilities.getCallbackHandle(androidConfiguration.onStart);
      if (handle == null) {
        return false;
      }

      final service = LaudyouAppService();
      service._setupMain();
      final result = await _mainChannel.invokeMethod("configure", {
        "handle": handle.toRawHandle(),
        "is_foreground_mode": androidConfiguration.isForegroundMode,
        "auto_start_on_boot": androidConfiguration.autoStart,
      });

      return result ?? false;
    }

    if (Platform.isIOS) {
      final CallbackHandle? backgroundHandle =
          PluginUtilities.getCallbackHandle(iosConfiguration.onBackground);
      if (backgroundHandle == null) {
        return false;
      }

      final CallbackHandle? foregroundHandle =
          PluginUtilities.getCallbackHandle(iosConfiguration.onForeground);
      if (foregroundHandle == null) {
        return false;
      }

      final service = LaudyouAppService();
      service._setupMain();

      final result = await _mainChannel.invokeMethod(
        "configure",
        {
          "background_handle": backgroundHandle.toRawHandle(),
          "foreground_handle": foregroundHandle.toRawHandle(),
          "auto_start": iosConfiguration.autoStart,
        },
      );

      return result ?? false;
    }

    return false;
  }

  // Send data from UI to Service, or from Service to UI
  void sendData(Map<String, dynamic> data) async {
    if (!(await (isServiceRunning()))) {
      dispose();
      return;
    }

    if (_isFromInitialization) {
      _mainChannel.invokeMethod("sendData", data);
      return;
    }

    _backgroundChannel.invokeMethod("sendData", data);
  }

  // Set Foreground Notification Information
  // Only available when foreground mode is true
  void setNotificationInfo({String? title, String? content}) {
    if (Platform.isAndroid) {
      _backgroundChannel.invokeMethod("setNotificationInfo", {
        "title": title,
        "content": content,
      });
    }
  }

  // Set Foreground Mode
  // Only for Android
  void setForegroundMode(bool value) {
    if (Platform.isAndroid) {
      _backgroundChannel.invokeMethod("setForegroundMode", {
        "value": value,
      });
    }
  }

  Future<bool> isServiceRunning() async {
    if (_isMainChannel) {
      var result = await _mainChannel.invokeMethod("isServiceRunning");
      return result ?? false;
    } else {
      return _isRunning;
    }
  }

  // StopBackgroundService from Running
  void stopBackgroundService() {
    _backgroundChannel.invokeMethod("stopService");
    _isRunning = false;
  }

  void setAutoStartOnBootMode(bool value) {
    if (Platform.isAndroid) {
      _backgroundChannel.invokeMethod("setAutoStartOnBootMode", {
        "value": value,
      });
    }
  }

  final StreamController<Map<String, dynamic>?> _streamController =
      StreamController.broadcast();

  Stream<Map<String, dynamic>?> get onDataReceived => _streamController.stream;

  void dispose() {
    _streamController.close();
  }

  static Future<String?> get platformVersion async {
    final String? version = await _mainChannel
        .invokeMethod('getPlatformVersion', {"a": 1, "b": "json 데이타 전달"});
    return version;
  }

  static Future<bool?> checkPermissions(
      {SystemWindowPrefMode prefMode = SystemWindowPrefMode.DEFAULT}) async {
    // return await _mainChannel.invokeMethod('checkPermissions',
    //     {"prefMode": Commons.getSystemWindowPrefMode(prefMode)});
    return await _mainChannel.invokeMethod(
        'checkPermissions', [Commons.getSystemWindowPrefMode(prefMode)]);
  }

  static Future<bool?> requestPermissions(
      {SystemWindowPrefMode prefMode = SystemWindowPrefMode.DEFAULT}) async {
    // return await _mainChannel.invokeMethod('requestPermissions',
    //     {"prefMode": Commons.getSystemWindowPrefMode(prefMode)});

    return await _mainChannel.invokeMethod(
        'requestPermissions', [Commons.getSystemWindowPrefMode(prefMode)]);
  }

  static Future<bool?> showSystemWindow({
    String notificationTitle = "Title",
    String notificationBody = "Body",
    SystemWindowPrefMode prefMode = SystemWindowPrefMode.DEFAULT,
  }) async {
    final Map<String, dynamic> params = <String, dynamic>{
      'header': {"text": "텍스트"},
      'body': 'body',
    };
    return await _mainChannel.invokeMethod('showSystemWindow', [
      notificationTitle,
      notificationBody,
      params,
      Commons.getSystemWindowPrefMode(prefMode)
    ]);
  }
}
