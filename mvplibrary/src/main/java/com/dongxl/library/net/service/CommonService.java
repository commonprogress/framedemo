package com.dongxl.library.net.service;

import com.dongxl.library.retrofit.BaseHttpResult;
import com.dongxl.library.utils.AppConstants;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by liukun on 2017/6/14.
 */

public interface CommonService {

    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Header("Range") String range, @Url String fileUrl);


    /**
     *
     * @param model 手机型号
     * @param imei  机器码
     * @param network   网络(wifi & 4G)
     * @return
     */
    @POST(AppConstants.DOWNLOAD_LOG)
    @FormUrlEncoded
    Observable<BaseHttpResult<Object>> downloadLog(@Field("model") String model, @Field("mac") String imei, @Field("network") String network);
}
