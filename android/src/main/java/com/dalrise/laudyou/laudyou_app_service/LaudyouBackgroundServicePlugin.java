package com.dalrise.laudyou.laudyou_app_service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.dalrise.laudyou.laudyou_app_service.services.BackgroundService;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.service.ServiceAware;
import io.flutter.embedding.engine.plugins.service.ServicePluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class LaudyouBackgroundServicePlugin extends BroadcastReceiver implements FlutterPlugin, MethodChannel.MethodCallHandler, ServiceAware {
    private BackgroundService service = new BackgroundService();

    @Override
    public void onAttachedToService(@NonNull ServicePluginBinding binding) {

    }

    @Override
    public void onDetachedFromService() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {

    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {

    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {

    }
}
