package com.example.myapplication;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.widget.Toast;

import com.dongxl.library.utils.AppFrontBackHelper;

public class DApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppFrontBackHelper helper = new AppFrontBackHelper();
        helper.register(DApplication.this, new AppFrontBackHelper.OnAppStatusListener() {
            @Override
            public void onFront() {
                //应用切到前台处理

            }

            @Override
            public void onBack() {
                //应用切到后台处理

            }
        });
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    /**
     * 判断level值是否等于TRIM_MEMORY_UI_HIDDEN，相等即表示app切换到了后台。
     *
     * @param level
     */
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Toast.makeText(this, "app进入后台运行", Toast.LENGTH_SHORT).show();
        }
    }

}
