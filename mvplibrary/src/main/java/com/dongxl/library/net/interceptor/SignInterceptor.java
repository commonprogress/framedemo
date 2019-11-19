package com.dongxl.library.net.interceptor;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.dongxl.library.utils.AppMaster;
import com.dongxl.library.utils.AppUtils;
import com.dongxl.library.utils.LogUtils;
import com.dongxl.library.utils.StringUtils;
import com.dongxl.library.utils.TimeManager;
import com.dongxl.library.utils.UrlEncodeUtils;

import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Created by liukun on 2017/4/5.
 * 向服务器请求添加基础公共参数
 */
public class SignInterceptor implements Interceptor {
    long minResponseTime = Long.MAX_VALUE;
    public static final String TAG = "Zhao";

    @Override
    public Response intercept(Chain chain) throws IOException {

//        long startTime = System.currentTimeMillis();
        TreeMap<String, String> mParamMap = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareTo(rhs);
            }
        });

        Request originRequest = chain.request();

        Request.Builder requestBuilder = originRequest.newBuilder();

        //添加基础参数
        addBasicParams(originRequest, requestBuilder, mParamMap);

        //添加header
        addHeader(originRequest, requestBuilder, mParamMap);
        Request request = requestBuilder.build();

//        if (AppMaster.getInstance().DEBUG) {

        long startTime = System.nanoTime();
        Response response = chain.proceed(request);
        long responseTime = System.nanoTime() - startTime;

        MediaType mediaType = response.body().contentType();
        String content = response.body().string();
        //如果这一次的请求响应时间小于上一次，则更新本地维护的时间
        if (responseTime <= minResponseTime) {
            // 同步服务器时间
            synchronizationServiceTime(response);
            minResponseTime = responseTime;
        }
//            String logResponseBody = content;
//
//            int length = logResponseBody.length();
//
//            int time = length / 500;
//
//            for (int i = 1; i <= time; i++) {
//                StringBuilder sb = new StringBuilder(logResponseBody);
//                sb.insert(500 * i + (i - 1), System.getProperty("line.separator"));
//            }

//        if (request.url().toString().contains("/cache/sysConfig")) {
//            LogUtils.d("get");
//        }


        content = content.replace(",\"data\":\"\"", "");
        content = content.replace(",\"data\":{}", "");
        content = content.replace(",\"data\":[]", "");

        if (AppUtils.isDebugable()) {
//            Log.i(TAG, "#############################################################");
////            Log.i(TAG, "request.url()=" + request.url());
////            Log.i(TAG, "response.code()=" + response.code());
////            Log.i(TAG, "response.body()=" + content);
            Log.i(TAG, "requestAll:" + request.url() + " " + response.code() + " " + content);
//            Log.i(TAG, "#############################################################");
        }
        if (response.code() == 401) {
            //错误码为401 表示token失效
//            UserState.setToken("");
//            BroadcastCenter.getInstance().action(BaseActionHolder.ACTION_TOKEN_FAIL).broadcast();
//            ModuleMaster.getInstance().mAppManager().goAppLogout();

        }

        return response.newBuilder().body(ResponseBody.create(mediaType, content)).build();
//        } else {
//            return chain.proceed(request);
//        }

//        return chain.proceed(request);

    }

    /**
     * 同步服务器时间，主要处理大神聊天时，插入本地消息与服务器时间不一致导致列表排序错乱
     */
    public static void synchronizationServiceTime(Response response) {
        try {
            Headers headers = response.headers();
            Date date = headers.getDate("Date");
            TimeManager.getInstance().initServerTime(date.getTime());
            LogUtils.i("当前服务器时间:" + date.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 添加HttpHeader
     *
     * @param requestBuilder
     */
    private synchronized void addHeader(Request originRequest, Request.Builder requestBuilder, TreeMap<String, String> mParamMap) {


//        requestBuilder.addHeader("Content-Type", "application/x-www-form-urlencoded");
//        requestBuilder.addHeader("Auth-Appkey", AppConstants.APP_ID);
//        long time = System.currentTimeMillis();
////        LogUtils.e(TAG, time + "");
//        requestBuilder.addHeader("Auth-Timestamp", time + "");
//
//        requestBuilder.addHeader("Auth-Sign", getSignString(originRequest, time, mParamMap));
//        requestBuilder.addHeader("Client-Info", ClientInfoUtils.getClientInfoUtils());
//        requestBuilder.addHeader("Auth-Country", !TextUtils.isEmpty(
//                SysUtils.CountryZipCode) ? SysUtils.CountryZipCode : SysUtils.getCountryZipCode());  //国家码
//        requestBuilder.addHeader("X-Branch",
//                SettingUtil.readString(AppMaster.getInstance().getAppContext(), MyConsts.ENV_BRANCH, "working"));
//        requestBuilder.addHeader("X-Env", AppMaster.getInstance().getEnvironmentName());
//        requestBuilder.addHeader("Auth-TimeZone", SysUtils.getTimeZone());
//
//        requestBuilder.addHeader("DPR", String.valueOf(DeviceUtils.getScreenDensity(AppMaster.getInstance().getAppContext())));// 设备屏幕 DPR
//        requestBuilder.addHeader("Viewport-Width", String.valueOf(DeviceUtils.getScreenWidth(AppMaster.getInstance().getAppContext()))); // 设备 viewport 宽度
//        requestBuilder.addHeader("Save-Data", DeviceUtils.getNetworkTypeImage(AppMaster.getInstance().getAppContext()));// 是否是省流量模式，wifi off，4g on

//        LogUtils.e(TAG, "addHeader originRequest.url() = " + originRequest.url());
    }

    /**
     * 添加基础参数
     *
     * @param request
     * @param requestBuilder
     */
    private void addBasicParams(Request request, Request.Builder requestBuilder, TreeMap<String, String> mParamMap) {
        if (request == null) {
            return;
        }

        String bodyToString = "";
        if ("GET".equals(request.method())) {
            //将参数添加到url后
            bodyToString = urlQueryToString(request);
        } else {
            //将参数转化为Map，添加基础参数，在转为body
            bodyToString = bodyToString(request);
        }

//        LogUtils.i(TAG, "requset body: " + bodyToString);

        //将参数字符串转换为map
        paramStrToMap(bodyToString, mParamMap);

        //添加基础参数到map
        builtBaseParams(mParamMap);
        if ("GET".equals(request.method())) {
            //将map在拼回url
            HttpUrl url = request.url().newBuilder().encodedQuery(mapToParamString(true, mParamMap)).build();

            requestBuilder.url(url);
        } else {
//            HttpUrl url = request.url();
//            Log.e(TAG, "SignInterceptor    SignInterceptor url: " + url);
            //将map转化为body
            requestBuilder.post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"),
                    mapToParamString(true, mParamMap)));
        }
    }

    /**
     * 将参数字符串转换为Map
     *
     * @param paramsStr
     * @return
     */
    private Map<String, String> paramStrToMap(String paramsStr, TreeMap<String, String> mParamMap) {

        if (TextUtils.isEmpty(paramsStr)) {
            return mParamMap;
        }

        String[] paramArray = paramsStr.split("&");

        if (paramArray == null) {
            return mParamMap;
        }

        for (int i = 0, size = paramArray.length; i < size; i++) {
            String[] entryStr = paramArray[i].split("=");
            if (entryStr != null && entryStr.length >= 2) {
                mParamMap.put(entryStr[0], entryStr[1]);
            }
        }

        return mParamMap;
    }

    /**
     * 将requestBody转换为String
     *
     * @param request
     * @return
     */
    private String bodyToString(Request request) {
        if (request == null) {
            return "";
        }
        Buffer buffer = new Buffer();
        try {
            request.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * 获取get请求中的请求参数
     *
     * @param request
     * @return
     */
    private String urlQueryToString(Request request) {
        if (request == null) {
            return "";
        }

        HttpUrl httpUrl = request.url();
        return httpUrl.query();
    }

    /**
     * 添加基本参数
     */
    private void builtBaseParams(TreeMap<String, String> mParamMap) {

//        mParamMap.put("appfrom", AppMaster.getInstance().getChannel());
//        mParamMap.put("time_zone", SysUtils.getTimeZone());
//        mParamMap.put("platform", "2");
//        mParamMap.put("region", DataCache.getInstance().getCurrLanguage());
//        mParamMap.put("appver", AppMaster.getInstance().getAppver());
//        mParamMap.put("realVer", AppMaster.getInstance().getVersionName());
//
//        mParamMap.put("version_code", AppMaster.getInstance().getVersionCode() + "");
//
//        String token = UserState.getToken();
//        if (!TextUtils.isEmpty(token)) {
//            mParamMap.put("token", token);
//        }
//        String userId = UserState.getUserId();
//        if (!TextUtils.isEmpty(userId)) {
//            mParamMap.put("user_id", userId);
//        }
//        String unlogin_token = SettingUtil.readString(AppMaster.getInstance().getAppContext(), MyConsts.KET_UN_LOGIN_KEY, "");
//        if (!StringUtils.isEmpty(unlogin_token)) {
//            mParamMap.put("unlogin_token", unlogin_token);
//        }

    }

    /**
     * 将map转换为string
     *
     * @return
     */
    private String mapToParamString(boolean needSeparator, TreeMap<String, String> mParamMap) {
        StringBuilder sb = new StringBuilder();

        Iterator<String> i = mParamMap.keySet().iterator();
        while (i.hasNext()) {
            String key = i.next();
            String val = mParamMap.get(key);

            if (null != val) {
                //由于框架默认添加了encode，需要先decode一遍
                val = Uri.decode(val);
                try {
                    if (UrlEncodeUtils.isUtf8Url(val)) {
                        val = Uri.decode(val);
                    }
                } catch (Exception ex) {

                }
                if (null != val) {
                    //由于服务端与java自带的encode标准不一致，调用自定义的encode工具
                    sb.append(key + "=" + UrlEncodeUtils.encode(val));
                }

                if (needSeparator && i.hasNext()) {
                    sb.append("&");
                }
            }
        }
//        LogUtils.d(TAG, "SignInterceptor    SignInterceptor: " + sb.toString());

        return sb.toString();
    }

    /**
     * 生成签名
     *
     * @param originRequest
     * @param time
     * @return
     */
//    protected String getSignString(Request originRequest, long time, TreeMap<String, String> mParamMap) {
//        StringBuilder sb = new StringBuilder();
//
//        if ("GET".equals(originRequest.method())) {
//            sb.append("GET#");
//        } else {
//            sb.append("POST#");
//        }
//        sb.append(originRequest.url().encodedPath() + "#");
//        sb.append(time + "#");
//        sb.append(AppConstants.APP_ID + "#");
//        sb.append("omg#");
//        sb.append(AppConstants.APP_SECRET + "#");
//
//        sb.append(mapToParamString(false, mParamMap));
//
//        String signStr = sb.toString();
////        Log.e(TAG, "signStr=" + signStr);
//        return SysUtils.stringToMD5(signStr);
//    }
}
