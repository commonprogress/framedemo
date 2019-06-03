package com.dongxl.library.retrofit;

import io.reactivex.functions.Function;


/**
 * Created by liukun on 2017/4/17.
 * 统一处理Http请求中，如果errorCode不为0，则抛出Api异常
 * 如果errorCode为0，返回data部分数据
 */
public class HttpResultFunc<T> implements Function<BaseHttpResult<T>, T> {
    @Override
    public T apply(BaseHttpResult<T> baseHttpResult) throws Exception {
        int errorCode = baseHttpResult.getErrcode();

        if (errorCode == 0) {
//            if (baseHttpResult.getData() == null) {
//                throw new InfoCaller(errorCode, baseHttpResult.getErrmsg());
//            } else {
            return baseHttpResult.getData();
//            }
        } else {
            throw new ApiException(errorCode, baseHttpResult.getErrmsg(), baseHttpResult.getData());
        }
    }
}
