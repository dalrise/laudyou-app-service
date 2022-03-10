import 'package:device_info_plus/device_info_plus.dart';
import 'package:flutter/material.dart';
import 'dart:async';
import 'dart:io';
import 'package:flutter/services.dart';
import 'package:laudyou_app_service/laudyou_app_service.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String? _activityResult = 'Unknown';

  @override
  void initState() {
    super.initState();
  }

  Future<void> getLockScreenActivity() async {
    String? imagePath;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      _activityResult = (await LaudyouAppService.activityLockScreen);
      print("$_activityResult");
    } on PlatformException catch (e) {
      _activityResult = e.toString();
    }

    print(_activityResult);
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin Laudyou App Service sample app'),
        ),
        body: Center(
          child: Column(
            children: [
              Text('안드로이드 호출 결과: $_activityResult\n'),
              ElevatedButton(
                child: const Text("안드로이드 화면 호출"),
                onPressed: getLockScreenActivity,
              ),
            ],
          ),
        ),
      ),
    );
  }
}
