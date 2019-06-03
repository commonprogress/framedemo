package com.dongxl.library.net.service;

import com.dongxl.library.retrofit.BaseHttpResult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by dongxl on 2017/11/6.
 */

public interface CustomService {
    /**
     * 自定义分享
     *
     * @param userId
     * @param token
     * @param url
     * @return
     */
    @GET
    Observable<BaseHttpResult<Object>> reqCustomShare(@Url String httpsUrl, @Query("user_id") String userId, @Query("token") String token, @Query("url") String url);


}
