package com.dongxl.library.net.interceptor;

import android.net.Uri;
import android.text.TextUtils;

import com.dongxl.library.utils.AppUtils;
import com.dongxl.library.utils.LogUtils;
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
public class TengXunSignInterceptor implements Interceptor {
    long minResponseTime = Long.MAX_VALUE;
    public static final String TAG = TengXunSignInterceptor.class.getSimpleName();

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
        if (AppUtils.isDebugable()) {
            LogUtils.d("RR", "#############################################################");
            LogUtils.d("RR", "request.url()=" + request.url());
            LogUtils.d("RR", "response.code()=" + response.code());
            LogUtils.d("RR", "response.body()=" + content);
            LogUtils.d("RR", "#############################################################");
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

        LogUtils.e(TAG, "requset body: " + bodyToString);

        //将参数字符串转换为map
        paramStrToMap(bodyToString, mParamMap);

        //添加基础参数到map
        if ("GET".equals(request.method())) {
            //将map在拼回url
            HttpUrl url = request.url().newBuilder().encodedQuery(mapToParamString(true, mParamMap)).build();

            requestBuilder.url(url);
        } else {
            HttpUrl url = request.url();
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
        LogUtils.e(TAG, "SignInterceptor    SignInterceptor: " + sb.toString());

        return sb.toString();
    }


}
