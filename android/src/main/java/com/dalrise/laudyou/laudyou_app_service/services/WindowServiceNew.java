package com.dalrise.laudyou.laudyou_app_service.services;

import static com.dalrise.laudyou.laudyou_app_service.utils.Constants.INTENT_EXTRA_PARAMS_MAP;
import static com.dalrise.laudyou.laudyou_app_service.utils.Constants.KEY_BODY;
import static com.dalrise.laudyou.laudyou_app_service.utils.Constants.KEY_FOOTER;
import static com.dalrise.laudyou.laudyou_app_service.utils.Constants.KEY_HEADER;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.dalrise.laudyou.laudyou_app_service.LaudyouAppServicePlugin;
import com.dalrise.laudyou.laudyou_app_service.R;
import com.dalrise.laudyou.laudyou_app_service.utils.Commons;
import com.dalrise.laudyou.laudyou_app_service.views.HeaderView;

import java.util.HashMap;
import java.util.Map;
public class WindowServiceNew  extends Service implements View.OnTouchListener {

    private static final String TAG = WindowServiceNew.class.getSimpleName();
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static int NOTIFICATION_ID = 1;
    public static final String INTENT_EXTRA_IS_UPDATE_WINDOW = "IsUpdateWindow";
    public static final String INTENT_EXTRA_IS_CLOSE_WINDOW = "IsCloseWindow";

    private WindowManager wm;
    private Context mContext;
    private LinearLayout windowView;
    private LinearLayout headerView;
    private LinearLayout bodyView;
    private LinearLayout footerView;

    private String windowGravity;
    private int windowWidth;
    private int windowHeight;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, LaudyouAppServicePlugin.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Overlay window service is running")
                .setSmallIcon(R.drawable.ic_bg_service_small)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(NOTIFICATION_ID, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return null;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        if (null != intent && intent.getExtras() != null) {
            Log.d(TAG, "onStartCommand1");
            @SuppressWarnings("unchecked")
            HashMap<String, Object> paramsMap = (HashMap<String, Object>) intent.getSerializableExtra(INTENT_EXTRA_PARAMS_MAP);
            Log.d(TAG, "onStartCommand2");
            mContext = this;
            boolean isCloseWindow = intent.getBooleanExtra(INTENT_EXTRA_IS_CLOSE_WINDOW, false);
            Log.d(TAG, "onStartCommand3");
            if (!isCloseWindow) {
                Log.d(TAG, "onStartCommand44");
                boolean isUpdateWindow = intent.getBooleanExtra(INTENT_EXTRA_IS_UPDATE_WINDOW, false);
                Log.d(TAG, "onStartCommand5");
                if (wm != null && isUpdateWindow && windowView != null) {
                    //updateWindow(paramsMap);
                } else {
                    createWindow(paramsMap);
                }
            }
        }
        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setWindowView(WindowManager.LayoutParams params, boolean isCreate) {
        boolean isEnableDraggable = true;//params.width == WindowManager.LayoutParams.MATCH_PARENT;
        if (isCreate) {
            windowView = new LinearLayout(mContext);
        }
        windowView.setOrientation(LinearLayout.VERTICAL);
        windowView.setBackgroundColor(Color.YELLOW);
        windowView.setLayoutParams(params);
        windowView.removeAllViews();
        windowView.addView(headerView);
//        if (bodyView != null)
//            windowView.addView(bodyView);
//        if (footerView != null)
//            windowView.addView(footerView);
        if (isEnableDraggable)
            windowView.setOnTouchListener(this);
    }

    private void createWindow(HashMap<String, Object> paramsMap) {
        closeWindow(false);
        Log.d(TAG, "createWindow-1");
        setWindowManager();
        Log.d(TAG, "createWindow-2");
        setWindowLayoutFromMap(paramsMap);
        WindowManager.LayoutParams params = getLayoutParams();
        Log.d(TAG, "createWindow-3");
        setWindowView(params, true);
        Log.d(TAG, "createWindow-4");
        try {
            wm.addView(windowView, params);
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
            retryCreateWindow(paramsMap);
        }
    }

    private void retryCreateWindow(HashMap<String, Object> paramsMap) {
        if(wm != null){
            wm.removeViewImmediate(windowView);
        }
        closeWindow(false);
        setWindowManager();
        setWindowLayoutFromMap(paramsMap);
        WindowManager.LayoutParams params = getLayoutParams();
        setWindowView(params, true);
        try {
            wm.addView(windowView, params);
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
    }

    private void setWindowLayoutFromMap(HashMap<String, Object> paramsMap) {
        Map<String, Object> headersMap = Commons.getMapFromObject(paramsMap, KEY_HEADER);
//        Map<String, Object> bodyMap = Commons.getMapFromObject(paramsMap, KEY_BODY);
//        Map<String, Object> footerMap = Commons.getMapFromObject(paramsMap, KEY_FOOTER);
//        Map<String, Object> headersMap = new ArrayMap<>();
//        Log.d(TAG, headersMap.toString());
        //windowMargin = UiBuilder.getInstance().getMargin(mContext, paramsMap.get(KEY_MARGIN));
//        windowGravity = (String) paramsMap.get(KEY_GRAVITY);
//        windowWidth = NumberUtils.getInt(paramsMap.get(KEY_WIDTH));
//        windowHeight = NumberUtils.getInt(paramsMap.get(KEY_HEIGHT));
        headerView = new HeaderView(mContext, headersMap).getView();
//        if (bodyMap != null)
//            bodyView = new BodyView(mContext, bodyMap).getView();
//        if (footerMap != null)
//            footerView = new FooterView(mContext, footerMap).getView();
    }

    private void closeWindow(boolean isEverythingDone) {
        Log.i(TAG, "Closing the overlay window");
        try {
            if (wm != null) {
                if (windowView != null) {
                    wm.removeView(windowView);
                    windowView = null;
                }
            }
            wm = null;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "view not found");
        }
        if (isEverythingDone) {
            stopSelf();
        }
    }

    private void setWindowManager() {
        if (wm == null) {
            wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        }
    }

    private WindowManager.LayoutParams getLayoutParams() {
        final WindowManager.LayoutParams params;
        params = new WindowManager.LayoutParams();
        params.width = (windowWidth == 0) ? android.view.WindowManager.LayoutParams.MATCH_PARENT : Commons.getPixelsFromDp(mContext, windowWidth);
        params.height = (windowHeight == 0) ? android.view.WindowManager.LayoutParams.WRAP_CONTENT : Commons.getPixelsFromDp(mContext, windowHeight);
        params.format = PixelFormat.TRANSLUCENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            params.flags = android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        } else {
            params.type = android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            params.flags = android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        }
        params.gravity = Commons.getGravity(windowGravity, Gravity.TOP);
//        int marginTop = windowMargin.getTop();
//        int marginBottom = windowMargin.getBottom();
//        int marginLeft = windowMargin.getLeft();
//        int marginRight = windowMargin.getRight();
//        params.x = Math.max(marginLeft, marginRight);
//        params.y = (params.gravity == Gravity.TOP) ? marginTop :
//                (params.gravity == Gravity.BOTTOM) ? marginBottom : Math.max(marginTop, marginBottom);

        return params;
    }
}
