package com.dalrise.laudyou.laudyou_app_service.base;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    String TAG = this.getClass().getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(provideContentViewId());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initPresenter();
        prepare();
    }

    protected abstract int provideContentViewId();

    protected abstract void initPresenter();

    protected abstract void prepare();
}
