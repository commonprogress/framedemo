package com.dongxl.library.net.retrofitutil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.dongxl.library.net.interceptor.SignInterceptorPluto;
import com.dongxl.library.utils.AppMaster;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by luy on 2017/7/5.
 * 目前用于通讯录上传
 */

public class RetrofitHolderPluto {

    private String mBaseUrl;

    private static Retrofit mRetrofit;
    private OkHttpClient client;

    private RetrofitHolderPluto() {
        mBaseUrl = AppMaster.getInstance().getServicePlutoAddr();

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
//                builder.addInterceptor(new LoggingInterceptor());
//            }
            HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
            client = builder
                    .addInterceptor(new SignInterceptorPluto())
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                    .hostnameVerifier(new HttpsUtils.AllHostnameVerifier())
                    .retryOnConnectionFailure(true)
                    .build();
        }

        return client;
    }

    public static Retrofit getRetrofitInstance() {
        if (mRetrofit == null) {
            synchronized (RetrofitHolder.class) {
                new RetrofitHolderPluto();
            }
        }
        return mRetrofit;
    }
}
