package com.dongxl.library.net.retrofitutil;

import com.dongxl.library.net.interceptor.SignInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.dongxl.library.net.interceptor.SignInterceptorPluto;
import com.dongxl.library.utils.AppMaster;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by liukun on 2017/4/19.
 */

public class RetrofitHolder {

    private String mBaseUrl;

    private static Retrofit mRetrofit;
    private  static OkHttpClient client;

    private RetrofitHolder() {

        mBaseUrl = AppMaster.getInstance().getServiceAddr();

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

    public static OkHttpClient getClient() {
        if (client == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
            client = builder
                    .addInterceptor(new SignInterceptor())
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
//                    .certificatePinner(HttpsUtils.getCertificatePinner());
//                    .connectionSpecs(Collections.singletonList(HttpsUtils.getConnectionSpec()))
//                    .connectionSpecs(HttpsUtils.createModernConnectionSpec())
                    .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                    .hostnameVerifier(new HttpsUtils.AllHostnameVerifier())
                    .build();
        }

        return client;
    }

    public static Retrofit getRetrofitInstance() {
        if (mRetrofit == null) {
            synchronized (RetrofitHolder.class) {
                new RetrofitHolder();
            }
        }
        return mRetrofit;
    }
}
