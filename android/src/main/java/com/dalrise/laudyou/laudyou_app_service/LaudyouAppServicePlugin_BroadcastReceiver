package com.dalrise.laudyou.laudyou_app_service;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.dalrise.laudyou.laudyou_app_service.utils.Commons;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.embedding.engine.plugins.service.ServiceAware;
import io.flutter.embedding.engine.plugins.service.ServicePluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.common.JSONMethodCodec;

/** LaudyouAppServicePlugin */
public class LaudyouAppServicePlugin extends BroadcastReceiver implements FlutterPlugin, MethodCallHandler, ServiceAware {



  private static final String TAG = "BackgroundServicePlugin";
  private static final List<LaudyouAppServicePlugin> _instances = new ArrayList<>();
  public int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1237;

  public LaudyouAppServicePlugin(){
    _instances.add(this);
  }

  private LaudyouAppServicePlugin(Context context, Activity activity) {
    this.context = context;
    mActivity = activity;
  }

  private MethodChannel channel;
  private Context context;
  private Activity mActivity;
  private BackgroundService service;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    this.context = flutterPluginBinding.getApplicationContext();
    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this.context);
    localBroadcastManager.registerReceiver(this, new IntentFilter("id.flutter/background_service"));

    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "id.flutter/background_service", JSONMethodCodec.INSTANCE);
    channel.setMethodCallHandler(this);
  }

  public static void registerWith(Registrar registrar) {
    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(registrar.context());
    final LaudyouAppServicePlugin plugin = new LaudyouAppServicePlugin();
    localBroadcastManager.registerReceiver(plugin, new IntentFilter("id.flutter/background_service"));

    final MethodChannel channel = new MethodChannel(registrar.messenger(), "id.flutter/background_service", JSONMethodCodec.INSTANCE);
    channel.setMethodCallHandler(plugin);
    plugin.channel = channel;

    new LaudyouAppServicePlugin(registrar.context(), registrar.activity());
  }

  private static void configure(Context context, long callbackHandleId, boolean isForeground, boolean autoStartOnBoot) {
    SharedPreferences pref = context.getSharedPreferences("id.flutter.background_service", MODE_PRIVATE);
    pref.edit()
            .putLong("callback_handle", callbackHandleId)
            .putBoolean("is_foreground", isForeground)
            .putBoolean("auto_start_on_boot", autoStartOnBoot)
            .apply();
  }

  private void start() {
    BackgroundService.enqueue(context);
    boolean isForeground = BackgroundService.isForegroundService(context);
    Intent intent = new Intent(context, BackgroundService.class);
    if (isForeground){
      ContextCompat.startForegroundService(context, intent);
    } else {
      context.startService(intent);
    }
  }


  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    String method = call.method;
    JSONObject arg = (JSONObject) call.arguments;

    Log.d(TAG, "onMethodCall:"+ method);

    try {
      if (method.equals("getPlatformVersion")) {
        result.success("Android " + android.os.Build.VERSION.RELEASE + arg.getString("b"));
        return;
      }

      if (method.equals("requestPermissions")) {
        String prefMode = arg.getString("prefMode");
        if (prefMode == null) {
          prefMode = "default";
        }
        if (askPermission(!isBubbleMode(prefMode))) {
          result.success(true);
        } else {
          result.success(false);
        }
        return;
      }

      if (method.equals("checkPermissions")) {
        String prefMode = arg.getString("prefMode");
        if (prefMode == null) {
          prefMode = "default";
        }
        if (checkPermission(!isBubbleMode(prefMode))) {
          result.success(true);
        } else {
          result.success(false);
        }
        return;
      }

      if ("configure".equals(method)) {
        long callbackHandle = arg.getLong("handle");
        boolean isForeground = arg.getBoolean("is_foreground_mode");
        boolean autoStartOnBoot = arg.getBoolean("auto_start_on_boot");

        configure(context, callbackHandle, isForeground, autoStartOnBoot);
        if (autoStartOnBoot){
          start();
        }

        result.success(true);
        return;
      }

      if ("start".equals(method)){
        start();
        result.success(true);
        return;
      }

      if (method.equalsIgnoreCase("sendData")) {
        for (LaudyouAppServicePlugin plugin : _instances) {
          if (plugin.service != null) {
            plugin.service.receiveData((JSONObject) call.arguments);
            break;
          }
        }

        result.success(true);
        return;
      }

      if (method.equalsIgnoreCase("isServiceRunning")) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
          if (BackgroundService.class.getName().equals(service.service.getClassName())) {
            result.success(true);
            return;
          }
        }
        result.success(false);
        return;
      }

      result.notImplemented();
    }catch (Exception e){
      result.error("100", "Failed read arguments", null);
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public boolean askPermission(boolean isOverlay) {
    if (!isOverlay && (Commons.isForceAndroidBubble(context) || Build.VERSION.SDK_INT > Build.VERSION_CODES.Q)) {
      //return NotificationHelper.getInstance(mContext).areBubblesAllowed();
      return true;
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (!Settings.canDrawOverlays(context)) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + context.getPackageName()));
        if (mActivity == null) {
          if (context != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            Toast.makeText(context, "Please grant, Can Draw Over Other Apps permission.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Can't detect the permission change, as the mActivity is null");
          } else {
            Log.e(TAG, "'Can Draw Over Other Apps' permission is not granted");
            Toast.makeText(context, "Can Draw Over Other Apps permission is required. Please grant it from the app settings", Toast.LENGTH_LONG).show();
          }
        } else {
          mActivity.startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
        }
      } else {
        return true;
      }
    }
    return false;
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public boolean checkPermission(boolean isOverlay) {
    if (!isOverlay && (Commons.isForceAndroidBubble(context) || Build.VERSION.SDK_INT > Build.VERSION_CODES.Q)) {
      //return NotificationHelper.getInstance(mContext).areBubblesAllowed();
      return true;
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return Settings.canDrawOverlays(context);
    }
    return false;
  }

  private boolean isBubbleMode(String prefMode) {
    boolean isPreferOverlay = "overlay".equalsIgnoreCase(prefMode);
    return Commons.isForceAndroidBubble(context) ||
            (!isPreferOverlay && ("bubble".equalsIgnoreCase(prefMode) || Build.VERSION.SDK_INT >= Build.VERSION_CODES.R));
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);

    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this.context);
    localBroadcastManager.unregisterReceiver(this);
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent.getAction() == null) return;

    if (intent.getAction().equalsIgnoreCase("id.flutter/background_service")){
      String data = intent.getStringExtra("data");
      try {
        JSONObject jData = new JSONObject(data);
        if (channel != null){
          channel.invokeMethod("onReceiveData", jData);
        }
      }catch (JSONException e){
        e.printStackTrace();
      } catch (Exception e){
        e.printStackTrace();
      }
    }
  }

  @Override
  public void onAttachedToService(@NonNull ServicePluginBinding binding) {
    Log.d(TAG, "onAttachedToService");

    this.service = (BackgroundService) binding.getService();
  }

  @Override
  public void onDetachedFromService() {
    this.service = null;
    Log.d(TAG, "onDetachedFromService");
  }
}
