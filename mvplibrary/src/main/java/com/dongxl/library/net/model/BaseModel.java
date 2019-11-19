/*
 * Copyright (c) 2018.
 * Author：Zhao
 * Email：joeyzhao1005@gmail.com
 */

package com.dongxl.library.net.model;


import com.google.gson.GsonBuilder;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

public class BaseModel {

    /**
     * 控制线程，并发起订阅
     * <p>
     * 不再推荐使用该方法，因为关闭界面的时候要去手动cancel掉request，不然可能会造成内存泄漏
     * <p>
     * 推荐使用
     * <p>
     *
     * @param observable
     * @param observer
     * @param <T>
     */
    protected <T> void makeSubscribe(Observable<T> observable, Observer<T> observer) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    protected RequestBody jsonRequestBody(Map map) {
        if (map == null || map.size() <= 0) {
            return RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), "");
        }
        String string = new GsonBuilder().create().toJson(map);
        return RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), string);
    }

}
