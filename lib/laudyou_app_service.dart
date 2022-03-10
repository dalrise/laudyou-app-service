import 'dart:async';
import 'dart:io';
import 'dart:ui';

import 'package:flutter/services.dart';

import 'utils/constants.dart';

class LaudyouAppService {
  static const MethodChannel _backgroundChannel =
      MethodChannel(Constants.BACKGROUND_CHANNEL, JSONMethodCodec());

  static const MethodChannel _mainChannel =
      MethodChannel(Constants.CHANNEL, JSONMethodCodec());

  static Future<String?> get activityLockScreen async {
    final String? result =
        await _mainChannel.invokeMethod('activity_lockscreen');
    return result;
  }
}
