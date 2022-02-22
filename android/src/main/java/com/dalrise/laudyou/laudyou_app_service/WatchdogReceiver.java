package com.dalrise.laudyou.laudyou_app_service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.dalrise.laudyou.laudyou_app_service.services.BackgroundService;

public class WatchdogReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(!BackgroundService.isManuallyStopped(context)){
            if (BackgroundService.isForegroundService(context)){
                ContextCompat.startForegroundService(context, new Intent(context, BackgroundService.class));
            } else {
                context.startService(new Intent(context, BackgroundService.class));
            }
        }
    }
}
