package com.dongxl.library.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import java.util.List;

/**
 * 应用前后台状态监听帮助类，仅在Application中使用
 */
public class AppFrontBackHelper {

    public static final String TAG = AppFrontBackHelper.class.getSimpleName();

    private OnAppStatusListener mOnAppStatusListener;


    public AppFrontBackHelper() {

    }

    /**
     * 注册状态监听，仅在Application中使用
     *
     * @param application
     * @param listener
     */
    public void register(Application application, OnAppStatusListener listener) {
        mOnAppStatusListener = listener;
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    public void unRegister(Application application) {
        application.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    private Application.ActivityLifecycleCallbacks activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        //打开的Activity数量统计
        private int activityStartCount = 0;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            LogUtils.e(TAG, "onActivityCreated==activity==" + activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            activityStartCount++;
            LogUtils.e(TAG, "onActivityStarted==activityStartCount==" + activityStartCount + "==activity==" + activity);
            //数值从0变到1说明是从后台切到前台
            if (activityStartCount == 1) {
                //从后台切到前台
                if (mOnAppStatusListener != null) {
                    mOnAppStatusListener.onFront();
                }
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
            LogUtils.e(TAG, "onActivityResumed==activity==" + activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {
            LogUtils.e(TAG, "onActivityPaused==activity==" + activity);
        }

        @Override
        public void onActivityStopped(Activity activity) {
            activityStartCount--;
            LogUtils.e(TAG, "onActivityStopped==activityStartCount==" + activityStartCount + "==activity==" + activity);
            //数值从1到0说明是从前台切到后台
            if (activityStartCount == 0) {
                //从前台切到后台
                if (mOnAppStatusListener != null) {
                    mOnAppStatusListener.onBack();
                }
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            LogUtils.e(TAG, "onActivitySaveInstanceState==activity==" + activity);
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            LogUtils.e(TAG, "onActivityDestroyed==activity==" + activity);
        }
    };


    /**
     * 判断程序前后台
     * @param context
     * @return true 后台
     */
    public static boolean isApplicationInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                //前台程序
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }



    public interface OnAppStatusListener {
        void onFront();

        void onBack();
    }
}


