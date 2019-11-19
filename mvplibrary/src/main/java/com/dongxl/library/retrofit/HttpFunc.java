/*
 * Copyright (c) 2018.
 * Author：Zhao
 * Email：joeyzhao1005@gmail.com
 */

package com.dongxl.library.retrofit;

import io.reactivex.functions.Function;


/**
 * 统一处理Http请求中，如果errorCode为0，数据没有，则抛出InfoCaller异常
 * 统一处理Http请求中，如果errorCode不为0，则抛出Api异常
 * 如果errorCode为0，返回data部分数据
 */
public class HttpFunc<T extends HttpResult> implements Function<T, T> {

    @Override
    public T apply(T result) throws Exception {
        int errorCode = result.getErrcode();
        if (errorCode == 0) {
            return result;
        } else {
            throw new ApiException(errorCode, result.getErrmsg(), result);
        }
    }
}
