package com.dongxl.library.net.retrofitutil;

import com.dongxl.library.net.interceptor.SignInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 自定义http域名
 * Created by dongxl on 2017/11/6.
 */

public class RetrofitCustomHolder {
    private static String mBaseUrl;

    private static Retrofit mRetrofit;
    private OkHttpClient client;

    public static void setBaseUrl(String baseUrl) {
        mBaseUrl = baseUrl;
    }

    private RetrofitCustomHolder() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private OkHttpClient getClient() {
        if (client == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
//            if (BuildConfig.DEBUG) {
//                builder.addNetworkInterceptor(new LoggingInterceptor());
//            }
            HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
            client = builder
                    .addInterceptor(new SignInterceptor())
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                    .hostnameVerifier(new HttpsUtils.AllHostnameVerifier())
                    .build();
        }

        return client;
    }

    public static Retrofit getRetrofitInstance() {
//        if (mRetrofit == null) {
            synchronized (RetrofitCustomHolder.class) {
                new RetrofitCustomHolder();
            }
//        }
        return mRetrofit;
    }
}
