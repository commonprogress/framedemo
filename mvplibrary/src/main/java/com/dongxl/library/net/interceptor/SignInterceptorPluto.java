package com.dongxl.library.net.interceptor;

import android.text.TextUtils;
import android.util.Log;

import com.dongxl.library.utils.AppConstants;
import com.dongxl.library.utils.AppMaster;
import com.dongxl.library.utils.AppUtils;
import com.dongxl.library.utils.LogUtils;
import com.dongxl.library.utils.TimeManager;

import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
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
public class SignInterceptorPluto implements Interceptor {
    long minResponseTime = Long.MAX_VALUE;
    public static final String TAG = "Zhao";

    @Override
    public Response intercept(Chain chain) throws IOException {

        TreeMap<String, String> mParamMap = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareTo(rhs);
            }
        });

        Request originRequest = chain.request();

        Request.Builder requestBuilder = originRequest.newBuilder();

        //添加header
        addHeader(originRequest, requestBuilder, mParamMap);

        logRequestBody(originRequest, requestBuilder);

        Request request = requestBuilder.build();

//        if (BuildConfig.DEBUG) {
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

        content = content.replace(",\"data\":\"\"", "");
        content = content.replace(",\"data\":{}", "");

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
        long time = System.currentTimeMillis();

////        requestBuilder.addHeader("Content-Type", "application/json;charset=utf-8");
////        LogUtils.d(TAG, time + "");
//        requestBuilder.addHeader("Auth-Appkey", AppConstants.APP_ID);
//        requestBuilder.addHeader("Auth-Timestamp", time + "");
//        String userId = UserState.getUserId();
//        if (!TextUtils.isEmpty(userId)) {
//            requestBuilder.addHeader("userId", userId);
//        }
//        requestBuilder.addHeader("Auth-Sign", buildNewSha1(time));
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
//
////        Log.e(TAG, "addHeader originRequest.url() = " + originRequest.url());
    }

    private void logRequestBody(Request request, Request.Builder requestBuilder) {

        String bodyToString = "";
        if ("GET".equals(request.method())) {
            //将参数添加到url后
            bodyToString = urlQueryToString(request);
        } else {
            //将参数转化为Map，添加基础参数，在转为body
            bodyToString = bodyToString(request);
        }

//        LogUtils.d(TAG, "request.body()=" + bodyToString);

        if ("GET".equals(request.method())) {
            //将map在拼回url
            HttpUrl url = request.url().newBuilder().encodedQuery(bodyToString).build();

            requestBuilder.url(url);
        } else {
            //将map转化为body
            requestBuilder.post(RequestBody.create(request.body().contentType(), bodyToString)).build();
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
     * 构建新的域名鉴权
     *
     * @return
     */
//    public static String buildNewSha1(long time) {
//        //Sha1("LYG:"+USERID+":TOKEN:"+token+":CT:"+Auth-Timestamp)
//        HashMap<String, String> newAddrParams = new HashMap<>();
//
//        String userId = UserState.getUserId();
//        if (!TextUtils.isEmpty(userId)) {
//            newAddrParams.put("userId", userId);
//        }
//
//        newAddrParams.put("AuthTimestamp", time + "");
//
//        String token = UserState.getToken();
//        if (!TextUtils.isEmpty(token)) {
//            newAddrParams.put("token", token);
//        }
//
//        StringBuilder sb = new StringBuilder();
//        sb.append("LYG:")
//                .append(newAddrParams.get("userId"))
//                .append(":TOKEN:")
//                .append(newAddrParams.get("token"))
//                .append(":CT:")
//                .append(newAddrParams.get("AuthTimestamp"));
//        return SysUtils.getSha1(sb.toString());
//    }
}
