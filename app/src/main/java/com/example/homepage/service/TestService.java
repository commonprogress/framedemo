package com.example.homepage.service;

import com.dongxl.library.retrofit.BaseHttpResult;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TestService {

    @GET("ss/ss")
    Observable<BaseHttpResult<String>> reqDemoGet();

    @GET("ss/ss")
    Observable<BaseHttpResult<String>> reqDemoGet(@Query("str") String str);

    @POST("ss/ss")
    @FormUrlEncoded
    Observable<BaseHttpResult<String>> reqDemoPost();

    @POST("ss/ss")
    @FormUrlEncoded
    Observable<BaseHttpResult<String>> reqDemoPost(@Field("str") String str);

    @POST("ss/ss")
    Observable<BaseHttpResult<String>> reqDemoPost1(@Body RequestBody body);
}
