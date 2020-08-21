package com.dongxl.library.mvp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layoutId = getResourceId();
        if (layoutId > 0) {
            setContentView(layoutId);
        }
    }

    protected abstract int getResourceId();
}
