/*
 * Copyright (c) 2018.
 * Author：Zhao
 * Email：joeyzhao1005@gmail.com
 */

package com.dongxl.library.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by joeyzhao on 2018/1/31.
 */

public class DeviceUtils {
    /**
     * 高
     */
    public static final int HIGH = 0;
    /**
     * 中
     */
    public static final int IN = 1;
    /**
     * 低
     */
    public static final int LOW = 2;

    public static String[] getCpuAbis() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return Build.SUPPORTED_ABIS;
        } else {
            String[] a = {Build.CPU_ABI};
            return a;
        }
    }


    // 获取CPU最大频率（单位KHZ）

    // "/system/bin/cat" 命令行

    // "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" 存储最大频率的文件的路径

    private static String maxCpuFreq;

    public static String getMaxCpuFreq() {
        if (!StringUtils.isEmptyOrNullStr(maxCpuFreq)) {
            return maxCpuFreq;
        }

        maxCpuFreq = "";
        ProcessBuilder cmd;

        try {
            String[] args = {"/system/bin/cat",

                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"};

            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];

            while (in.read(re) != -1) {
                maxCpuFreq = maxCpuFreq + new String(re);
            }

            in.close();

        } catch (IOException ex) {
            ex.printStackTrace();
            maxCpuFreq = "N/A";
        }

        return maxCpuFreq.trim();
    }


    // 获取CPU最小频率（单位KHZ）

    public static String getMinCpuFreq() {

        String result = "";

        ProcessBuilder cmd;

        try {

            String[] args = {"/system/bin/cat",

                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"};

            cmd = new ProcessBuilder(args);

            Process process = cmd.start();

            InputStream in = process.getInputStream();

            byte[] re = new byte[24];

            while (in.read(re) != -1) {

                result = result + new String(re);

            }

            in.close();

        } catch (IOException ex) {

            ex.printStackTrace();

            result = "N/A";

        }

        return result.trim();

    }


    // 实时获取CPU当前频率（单位KHZ）

    public static String getCurCpuFreq() {

        String result = "N/A";

        try {

            FileReader fr = new FileReader(

                    "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");

            BufferedReader br = new BufferedReader(fr);

            String text = br.readLine();

            result = text.trim();

        } catch (FileNotFoundException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        return result;

    }


    /**
     * 不准 对于模拟器来说
     *
     * @return
     */
    @Deprecated
    public static boolean isX86() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.SUPPORTED_ABIS == null) {
                return false;
            } else {
                List<String> cpus = Arrays.asList(Build.SUPPORTED_ABIS);
                if ((cpus.contains("x86") || cpus.contains("x86_64"))) {
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            if (Build.CPU_ABI != null && ("x86".equals(Build.CPU_ABI.trim()) || "x86_64".equals(Build.CPU_ABI.trim()))) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getApplicationContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        float fontScale = context.getApplicationContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int dip2px(Context context, int dipValue) {
        if (dipValue == 0) {
            return 0;
        }
        final float scale = getScreenDensity(context);
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        if (pxValue == 0) {
            return 0;
        }
        final float scale = getScreenDensity(context);
        return (int) (pxValue / scale + 0.5f);
    }


    public static int statusbarheight;

    //获取状态栏高度
    public static int getStatusBarHeight(Context context) {
        if (statusbarheight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusbarheight = context.getApplicationContext().getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (statusbarheight == 0) {
            statusbarheight = dip2px(context, 25);
        }
        return statusbarheight;
    }

    public static int navigationBarHeight = -1;

    //获取状态栏高度
    public static int getNavigationBarHeight(Context context) {
        if (navigationBarHeight < 0) {
            try {
                if (checkDeviceHasNavigationBar(context)) {
                    Class<?> c = Class.forName("com.android.internal.R$dimen");
                    Object o = c.newInstance();
                    Field field = c.getField("navigation_bar_height");
                    int x = (Integer) field.get(o);
                    navigationBarHeight = context.getApplicationContext().getResources().getDimensionPixelSize(x);
                } else {
                    navigationBarHeight = 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return navigationBarHeight;
    }

    //获取是否存在NavigationBar
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;
    }


    private static float screenDensity;

    /***
     * 获取屏幕密度
     *
     * @return
     */
    public static float getScreenDensity(Context context) {
        if (screenDensity != 0f) {
            return screenDensity;
        }
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        screenDensity = dm.density;
        return screenDensity;
    }


    /**
     * 获取当期连接的网络类型
     *
     * @return
     */
    public static int getNetworkType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == connectivityManager) {
            return NETWORK_TYPE_NONE;
        }
        NetworkInfo mobileNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifiNetworkInfo && wifiNetworkInfo.isConnected()) {
            return NETWORK_TYPE_WIFI;
        } else if (null != mobileNetworkInfo && mobileNetworkInfo.isConnected()) {
            return NETWORK_TYPE_MOBILE;
        } else {
            return NETWORK_TYPE_NONE;
        }
    }

    /**
     * 获取当期连接的网络类型
     *
     * @return
     */
    public static String getNetworkTypeName(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == connectivityManager) {
            return "none";
        }
        NetworkInfo mobileNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifiNetworkInfo && wifiNetworkInfo.isConnected()) {
            return wifiNetworkInfo.getTypeName();
        } else if (null != mobileNetworkInfo && mobileNetworkInfo.isConnected()) {
            return mobileNetworkInfo.getTypeName();
        } else {
            return "none";
        }
    }

    /**
     * 获取当期连接的网络类型，是否是省流量模式
     *
     * @return ，wifi off，4g on
     */
    public static String getNetworkTypeImage(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == connectivityManager) {
            return "on";
        }
        NetworkInfo mobileNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifiNetworkInfo && wifiNetworkInfo.isConnected()) {
            return "off";
        } else if (null != mobileNetworkInfo && mobileNetworkInfo.isConnected()) {
            return "on";
        } else {
            return "on";
        }
    }


    /**
     * 检测网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }

        return false;
    }


    public static final int NETWORK_TYPE_NONE = -1; //无网络
    public static final int NETWORK_TYPE_WIFI = 0x01;//wifi
    public static final int NETWORK_TYPE_MOBILE = 0x02;//移动网络


    /**
     * 检测Sdcard是否存在
     *
     * @return
     */
    public static boolean isExitsSdcard() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }


    private static int screen_width = 0;

    /***
     * 获取屏幕宽度
     *
     * @return
     */
    public static int getScreenWidth(Context context) {
        if (screen_width != 0) {
            return screen_width;
        }
        WindowManager wm = (WindowManager) context
                .getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        screen_width = wm.getDefaultDisplay().getWidth();
        screen_width = screen_width <= 0 ? 1080 : screen_width;
        return screen_width;
    }

//    private float screenDensity;
//
//    /***
//     * 获取屏幕密度
//     *
//     * @return
//     */
//    public static float getScreenDensity() {
//        Resources res = AppMaster.getInstance().getAppContext().getResources();
//        DisplayMetrics dm = res.getDisplayMetrics();
//        return dm.density;
//    }

    private static int screen_height;

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public static int getScreenHeight(Context context) {
        if (screen_height != 0) {
            return screen_height;
        }
        WindowManager wm = (WindowManager) context
                .getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        screen_height = wm.getDefaultDisplay().getHeight();
        screen_height = screen_height <= 0 ? 1920 : screen_height;
        return screen_height;
    }

    private static String _mac;

    /**
     * 获取mac地址
     *
     * @return
     */
    public static String getLocalMacAddress(Context context) {

        if (!StringUtils.isEmpty(_mac)) {
            return _mac;
        }

        //获取电话ID
        WifiManager wifi = (WifiManager) context
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        _mac = info.getMacAddress();
        if (!StringUtils.isEmpty(_mac)) {
            return _mac;
        }
        return "";
    }

    private static String uuid;

    /**
     * 获取唯一编码  imei
     *
     * @return
     */
    public static String getLocalImei(Context context) {
        if (!StringUtils.isEmpty(uuid)) {
            return uuid;
        }
        try {
            //获取电话ID
            TelephonyManager TelephonyMgr = (TelephonyManager) context
                    .getApplicationContext()
                    .getSystemService(Context.TELEPHONY_SERVICE);
            uuid = TelephonyMgr.getDeviceId();
            if (!StringUtils.isEmpty(uuid)) {
                return uuid;
            }
        } catch (Exception e) {
            //没有权限（权限在外部判断，跟着业务走，因为和界面想关联）
        }
        return "";
    }

    private static String comprehensiveId;

    /**
     * 拼装五个数据作为唯一标示的信息
     *
     * @return
     */
    public static String getComprehensiveId(Context context) {
        if (StringUtils.isEmpty(comprehensiveId)) {
            StringBuffer m_szLongID = new StringBuffer();
            m_szLongID.append(getLocalImei(context));
            m_szLongID.append(getPseudoUniqueId());
            m_szLongID.append(getLocalAndroidId(context));
            m_szLongID.append(getWlanMac());
            m_szLongID.append(getLocalMacAddress(context));
            comprehensiveId = m_szLongID.toString();
            return comprehensiveId;
        } else {
            return comprehensiveId;
        }
    }

    private static String _szDevIDShort;

    private static String getPseudoUniqueId() {
        if (!StringUtils.isEmpty(_szDevIDShort)) {
            return _szDevIDShort;
        }

        _szDevIDShort = "35" + Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +

                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +

                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 + Build.USER.length() % 10; //13 位
        return _szDevIDShort;
    }

    private static String mWlanMac;

    public static String getWlanMac() {
        if (!StringUtils.isEmpty(mWlanMac)) {
            return mWlanMac;
        }
        try {
            BluetoothAdapter m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (m_BluetoothAdapter != null) {
                mWlanMac = m_BluetoothAdapter.getAddress();
                if (!StringUtils.isEmpty(mWlanMac)) {
                    return mWlanMac;
                }
            }
        } catch (Exception e) {
            //没有权限（权限在外部判断，跟着业务走，因为和界面想关联）
        }
        return "";
    }

    private static String _imsi;

    /**
     * 获取  imsi
     *
     * @return
     */
    public static String getLocalImsi(Context context) {
        if (!StringUtils.isEmpty(_imsi)) {
            return _imsi;
        }
        try {

            //获取imsi
            TelephonyManager TelephonyMgr = (TelephonyManager) context
                    .getApplicationContext()
                    .getSystemService(Context.TELEPHONY_SERVICE);
            _imsi = TelephonyMgr.getSubscriberId();
            if (!StringUtils.isEmpty(_imsi)) {
                return _imsi;
            }
        } catch (Exception e) {
            //没有权限（权限在外部判断，跟着业务走，因为和界面想关联）
        }
        return "";
    }

    private static String android_id;

    /**
     * 获取Android id
     *
     * @return
     */
    public static String getLocalAndroidId(Context context) {
        try {
            if (!StringUtils.isEmpty(android_id)) {
                return android_id;
            }
            android_id = Settings.Secure.getString(
                    context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            return android_id;
        } catch (Exception e) {
            //没有权限（权限在外部判断，跟着业务走，因为和界面想关联）
        }
        return "";
    }

    /**
     * 获取网络名字
     *
     * @return
     */
    public static String getNetworkOperatorName(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getApplicationContext()
                .getSystemService(Context.TELEPHONY_SERVICE);

        String network_operationname = tm.getNetworkOperatorName();
        if (!StringUtils.isEmpty(network_operationname)) {
            return network_operationname;
        }
        return "";
    }


    /**
     * 获取设备名称
     *
     * @return
     */
    public static String getDeviceName() {
        return Build.MODEL;
    }

    private static String cpuName;

    /**
     * cpu名字
     *
     * @return
     */
    public static String getCpuName() {
        if (!StringUtils.isEmptyOrNullStr(cpuName)) {
            return cpuName;
        }
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split(":\\s+", 2);
            for (int i = 0; i < array.length; i++) {
            }
            cpuName = array[1];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cpuName;
    }

    /**
     * 内存大小
     */
    private static String totalMemory;

    public static String getTotalMemory() {
        if (!StringUtils.isEmptyOrNullStr(totalMemory)) {
            return totalMemory;
        }

        String str1 = "/proc/meminfo";
        totalMemory = "";
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            while ((totalMemory = localBufferedReader.readLine()) != null) {
                return totalMemory;
            }
        } catch (IOException e) {
        }
        return totalMemory;
    }


    /**
     * 获取应用哈希值
     *
     * @return
     */
    public static String getSignature(Context context) {
        String hash = "";
        PackageManager manager = context.getPackageManager();
        String pkgname = context.getPackageName();
        boolean isEmpty = pkgname.isEmpty();
        if (isEmpty) {
            Toast.makeText(context, "应用程序的包名不能为空！", Toast.LENGTH_SHORT).show();
        } else {
            try {

                PackageInfo packageInfo = manager.getPackageInfo(pkgname, PackageManager.GET_SIGNATURES);

                Signature[] signatures = packageInfo.signatures;
                Signature sign = signatures[0];

                byte[] signByte = sign.toByteArray();
                Log.e("getSingInfo", bytesToHexString(signByte));
                hash = bytesToHexString(generateSHA1(signByte));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        }
        return hash;
    }

    public static byte[] generateSHA1(byte[] data) {
        try {
            // 使用getInstance("算法")来获得消息摘要,这里使用SHA-1的160位算法
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            // 开始使用算法
            messageDigest.update(data);
            // 输出算法运算结果
            byte[] hashValue = messageDigest.digest(); // 20位字节
            return hashValue;
        } catch (Exception e) {
            Log.e("generateSHA1", e.getMessage());
        }
        return null;

    }

    public static String bytesToHexString(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        StringBuilder buff = new StringBuilder();
        for (byte aByte : bytes) {
            if ((aByte & 0xff) < 16) {
                buff.append('0');
            }
            buff.append(Integer.toHexString(aByte & 0xff));
        }
        return buff.toString();
    }

    /**
     * 获取蓝牙mac
     *
     * @param mContext
     * @return
     */
    public static String getBlueToothMac(Context mContext) {
        String macAddress = Settings.Secure.getString(mContext.getContentResolver(), "bluetooth_address");
        return macAddress;
    }

    /**
     * 获取手机mac地址
     *
     * @return
     */
    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }
                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

    private static String wifiSsid;

    /**
     * 获取wifi_ssid
     *
     * @return
     */
    public static String getWifiSsid(Context context) {

        if (!StringUtils.isEmpty(wifiSsid)) {
            return wifiSsid;
        }

        try {
            //获取电话ID
            WifiManager wifi = (WifiManager) context
                    .getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            wifiSsid = info.getSSID();
        } catch (Exception e) {
            //没有权限（权限在外部判断，跟着业务走，因为和界面想关联）
        }
        return !StringUtils.isEmpty(wifiSsid) ? wifiSsid : "0";
    }

    private static String wifiBssid;

    /**
     * 获取wifiBssid
     *
     * @return
     */
    public static String getWifiBssid(Context context) {

        if (!StringUtils.isEmpty(wifiBssid)) {
            return wifiBssid;
        }

        try {
            //获取电话ID
            WifiManager wifi = (WifiManager) context
                    .getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            wifiBssid = info.getBSSID();
        } catch (Exception e) {
            //没有权限（权限在外部判断，跟着业务走，因为和界面想关联）
        }
        return !StringUtils.isEmpty(wifiBssid) ? wifiBssid : "0";
    }

    private static List<ScanResult> scanResultList;

    /**
     * 获取wifiBssid
     *
     * @return
     */
    public static String getScanResultList(Context context) {
        if (scanResultList != null && !scanResultList.isEmpty()) {
            return new Gson().toJson(scanResultList);
        }

        try {
            //获取电话ID
            WifiManager wifi = (WifiManager) context
                    .getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            scanResultList = wifi.getScanResults(); // 扫描返回结果列表
        } catch (Exception e) {
            //没有权限（权限在外部判断，跟着业务走，因为和界面想关联）
        }
        return (scanResultList == null || scanResultList.isEmpty()) ? "" : new Gson().toJson(scanResultList);
    }

    /**
     * @param context
     * @return
     */
    public static int getScreenSize(Context context) {
        int screenWidth = DeviceUtils.getScreenWidth(context);

        if (screenWidth < 1080) {
            //720p
            return 0;
        } else if (screenWidth >= 1080 && screenWidth < 1440) {
            //1080p
            return 1;
        } else if (screenWidth >= 1440) {
            return 2;
        } else {
            return 1;
        }
    }


    /**
     * 暂定比例大于1.8为全面屏
     *
     * @param context
     * @return
     */
    public static boolean isScreenAspect(Context context) {
        float screenHeight = getScreenHeight(context);
        float screenWidth = getScreenWidth(context);
        return screenHeight / screenWidth > 1.8f;
    }
}
