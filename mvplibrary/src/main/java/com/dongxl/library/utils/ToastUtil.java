/*
 * Copyright (c) 2018.
 * Author：Zhao
 * Email：joeyzhao1005@gmail.com
 */

package com.dongxl.library.utils;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.StringRes;
import android.widget.Toast;

import com.dongxl.library.R;


/**
 * Toast工具类
 */
public class ToastUtil {
    private static Toast mToast = null;
    private static Resources mRes = null;
    private static String mPackageName = null;


    public static void s(Context context, String msg) {
        if (StringUtils.isEmptyOrNullStr(msg)) {
            return;
        }
    }

    public static void l(Context context, @StringRes int stringResId) {
        Toast.makeText(AppMaster.getInstance().getAppContext(), stringResId, Toast.LENGTH_LONG).show();
    }

    public static void l(@StringRes int stringResId) {
        Toast.makeText(AppMaster.getInstance().getAppContext(), stringResId, Toast.LENGTH_LONG).show();
    }

    public static void l(Context context, String msg) {
        if (StringUtils.isEmptyOrNullStr(msg)) {
            return;
        }
        Toast.makeText(AppMaster.getInstance().getAppContext(), msg, Toast.LENGTH_LONG).show();
    }

    public static void l(String msg) {
        if (StringUtils.isEmptyOrNullStr(msg)) {
            return;
        }
        Toast.makeText(AppMaster.getInstance().getAppContext(), msg, Toast.LENGTH_LONG).show();
    }

    /**
     * 弹出Toast
     *
     * @param toastMsg
     */
    @Deprecated
    public synchronized static void show(Context context, int toastMsg) {
        String msg = ResUtil.getString(toastMsg);
        if (!StringUtils.isEmpty(msg)) {
            showToast(AppMaster.getInstance().getAppContext(), msg);
        }
    }


    /**
     * 弹出Toasdt
     *
     * @param toastMsg
     */
    public synchronized static void show(int toastMsg) {
        String msg = AppMaster.getInstance().getAppContext().getString(toastMsg);
        if (!StringUtils.isEmpty(msg)) {
            showToast(AppMaster.getInstance().getAppContext(), msg);
        }
    }


    /**
     * 弹出Toast
     *
     * @param toastMsg 要打印的Toast信息
     */
    public synchronized static void showToast(final String toastMsg) {
        cancel();
        if (null != toastMsg) {
            final String tmp = toastMsg.toLowerCase();
            if (tmp.indexOf("exception") != -1 || tmp.indexOf("java") != -1 || tmp.indexOf("json") != -1) {
                mToast = Toast.makeText(AppMaster.getInstance().getAppContext(), ResUtil.getString(R.string.a_0005), Toast.LENGTH_SHORT);
            } else {
                mToast = Toast.makeText(AppMaster.getInstance().getAppContext(), toastMsg, Toast.LENGTH_SHORT);
            }
            mToast.show();
        }
    }

    /**
     * 弹出Toast
     *
     * @param toastMsg 要打印的Toast信息
     */
    @Deprecated
    public synchronized static void showToast(Context context, final String toastMsg) {
        cancel();
        if (null != toastMsg) {
            final String tmp = toastMsg.toLowerCase();
            if (tmp.indexOf("exception") != -1 || tmp.indexOf("java") != -1 || tmp.indexOf("json") != -1) {
                mToast = Toast.makeText(AppMaster.getInstance().getAppContext(), ResUtil.getString(R.string.a_0005), Toast.LENGTH_SHORT);
            } else {
                mToast = Toast.makeText(AppMaster.getInstance().getAppContext(), toastMsg, Toast.LENGTH_SHORT);
            }
            mToast.show();
        }
    }

    public synchronized static void cancel() {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = null;
    }
}
