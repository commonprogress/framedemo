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
 * Created by liukun on 2017/10/9.
 */

public class RetrofitDownloadHolder {
    private String mBaseUrl;

    private static Retrofit mRetrofit;
    private OkHttpClient client;

    private RetrofitDownloadHolder() {

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

    private OkHttpClient getClient() {
        if (client == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
//            if (BuildConfig.DEBUG) {
//                builder.addNetworkInterceptor(new LoggingInterceptor());
//            }
            HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
            client = builder
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    //.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                    //.hostnameVerifier(new HttpsUtils.AllHostnameVerifier())
                    .build();
        }

        return client;
    }

    public static Retrofit getRetrofitInstance() {
        if (mRetrofit == null) {
            synchronized (RetrofitDownloadHolder.class) {
                new RetrofitDownloadHolder();
            }
        }
        return mRetrofit;
    }
}
