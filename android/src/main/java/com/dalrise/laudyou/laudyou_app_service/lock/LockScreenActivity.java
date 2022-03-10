package com.dalrise.laudyou.laudyou_app_service.lock;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dalrise.laudyou.laudyou_app_service.R;
import com.dalrise.laudyou.laudyou_app_service.base.BaseActivity;

public class LockScreenActivity extends BaseActivity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_lock_screen);
//    }

    @Override
    public int provideContentViewId() {
        return R.layout.activity_lock_screen;
    }

    @Override
    public void initPresenter() {

        findViewById(R.id.buttonClassify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findViewById(R.id.buttonClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_OK, new Intent().putExtra("activityResult", "안드로이드 화면에서 전송한 데이타 입니다."));
                System.gc();
                finish();
            }
        });

    }

    @Override
    public void prepare() {

    }
}