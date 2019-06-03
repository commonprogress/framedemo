package com.dongxl.library.utils;

import android.app.Activity;

import java.util.Stack;

/**
 * Created by wang on 15/6/17.
 */
public class AppManager {
    private Stack<Activity> activityStack;
    private Stack<String> activityStackStr;
    private static AppManager instance;


    private AppManager() {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        if (activityStackStr == null) {
            activityStackStr = new Stack<String>();
        }
    }

    /**
     * 单一实例
     */
    public static AppManager getAppManager() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        activityStack.add(activity);
    }

    /**
     * 添加Activity名字到堆栈
     */
    public void addActivityStr(String activity) {
        activityStackStr.add(activity);
    }

    /**
     * 添加Activity名字到堆栈
     */
    public void addActivityStr(Activity activity) {
        addActivityStr(activity.getClass().getCanonicalName());
    }

    public void removeActivityStr(String activityName) {
        if (StringUtils.isEmpty(activityName)) {
            return;
        }
        activityStackStr.remove(activityName);
    }

    /**
     * 添加Activity到堆栈
     */
    public void removeActivity(Activity activity) {
        if (activityStack == null) {
            return;
        }
        if (null != activity) {
            activityStack.remove(activity);
            removeActivityStr(activity.getClass().getCanonicalName());
        }
    }

    /**
     * 添加Activity到堆栈
     */
    public void removeActivity(Class activityClass) {
        if (activityStack == null) {
            return;
        }
        if (activityClass == null || activityStack == null || (null != activityStack && activityStack.size() == 0)) {
            return;
        }
        int size = activityStack.size();
        for (int i = size - 1; i >= 0; i--) {
            Activity activity = activityStack.get(i);
            if (null != activity && activity.getClass().equals(activityClass)) {
                removeActivity(activity);
            }
        }
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        if (activityStack == null || activityStack.empty()) {
            return null;
        }
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public String currentActivityStr() {
        if (activityStackStr == null || activityStackStr.empty()) {
            return "";
        }
        String activityStr = activityStackStr.lastElement();
        return activityStr;
    }


    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        finishActivity(currentActivity());
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null && activityStack != null && !activityStack.empty()) {
            activityStack.remove(activity);
            removeActivityStr(activity.getClass().getCanonicalName());
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        if (cls == null || activityStack == null) {
            return;
        }
        Activity curractivity = null;
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                curractivity = activity;
                break;
            }
        }
        finishActivity(curractivity);
    }

    /**
     * 是否包含指定的activity
     */
    public boolean hasActivity(Class<?> cls) {
        if (cls == null || activityStack == null) {
            return false;
        }
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 结束指定类名的所有Activity
     */
    public void finishActivityOfClass(Class<?> cls) {
        if (cls == null || activityStack == null || (null != activityStack && activityStack.size() == 0)) {
            return;
        }
        int size = activityStack.size();
        for (int i = size - 1; i >= 0; i--) {
            if (activityStack.get(i).getClass().equals(cls)) {
                finishActivity(activityStack.get(i));
            }
        }

    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        if (activityStack == null) {
            return;
        }
        int size = activityStack.size();
        for (int i = 0; i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
        activityStackStr.clear();
    }

    /**
     * 结束所有Activity,除了指定的Activity
     */
    public void finishActivitysExceptAssign(Class<?> cls) {
        if (activityStack == null || cls == null) {
            return;
        }
        int size = activityStack.size();
        for (int i = size - 1; i >= 0; i--) {
            Activity activity = activityStack.get(i);
            if (null != activity && !(activity.getClass().equals(cls))) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity,除了指定的Activity
     */
    public void finishActivitysExceptAssign(Class<?> cls, Class<?> cls2) {
        if (activityStack == null || cls == null) {
            return;
        }
        int size = activityStack.size();
        for (int i = size - 1; i >= 0; i--) {
            Activity activity = activityStack.get(i);
            if (null != activity && !(activity.getClass().equals(cls)) && !(activity.getClass().equals(cls2))) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity,除了指定的Activity
     */
    public void finishActivitysExceptAssign(Class<?> cls, Class<?> cls2, Class<?> cls3) {
        if (activityStack == null || cls == null) {
            return;
        }
        int size = activityStack.size();
        for (int i = size - 1; i >= 0; i--) {
            Activity activity = activityStack.get(i);
            if (null != activity && !(activity.getClass().equals(cls)) && !(activity.getClass().equals(cls2)) && !(activity.getClass().equals(cls3))) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 退出应用程序
     */
//    public void AppExit(Context context) {
//        try {
//            finishAllActivity();
//            ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//            activityMgr.restartPackage(context.getPackageName());
//            System.exit(0);
//        } catch (Exception e) {
//        }
//    }
    public int getActivityCount() {
        if (activityStack == null) {
            return 0;
        }
        return activityStack.size();
    }
}
