import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:laudyou_app_service/laudyou_app_service.dart';

void main() {
  const MethodChannel channel = MethodChannel('laudyou_app_service');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await LaudyouAppService.platformVersion, '42');
  });
}
