import 'package:laudyou_app_service/laudyou_app_service.dart';

class Commons {
  static String getSystemWindowPrefMode(SystemWindowPrefMode prefMode) {
    //if (prefMode == null) prefMode = SystemWindowPrefMode.DEFAULT;
    switch (prefMode) {
      case SystemWindowPrefMode.OVERLAY:
        return "overlay";
      case SystemWindowPrefMode.BUBBLE:
        return "bubble";
      case SystemWindowPrefMode.DEFAULT:
      default:
        return "default";
    }
  }
}
