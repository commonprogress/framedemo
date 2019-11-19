package com.example.homepage.model;

import com.dongxl.library.net.model.BaseModel;
import com.dongxl.library.net.service.ServiceHolder;
import com.dongxl.library.retrofit.HttpResultFunc;
import com.example.homepage.service.TestService;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import okhttp3.RequestBody;

public class TestModel extends BaseModel {

    public static volatile TestModel instance;

    public TestModel() {
    }

    public static synchronized TestModel getInstance() {
        if (instance == null) {
            synchronized (TestModel.class) {
                if (instance == null) {
                    instance = new TestModel();
                }
            }
        }
        return instance;
    }

    public static void setInstanceNull() {
        instance = null;
    }

    private TestService getService() {
        return ServiceHolder.getInstance().getService(TestService.class);
//        return ServiceHolder.getInstance().getPlutoService(TestService.class);
    }

    public void testReqGet(Observer<String> observer) {
        Observable observable = getService().reqDemoGet().map(new HttpResultFunc<String>());
        makeSubscribe(observable, observer);
    }

    public void testReqGet(String str, Observer<String> observer) {
        Observable observable = getService().reqDemoGet(str).map(new HttpResultFunc<String>());
        makeSubscribe(observable, observer);
    }

    public void testReqPost(String str, Observer<String> observer) {
        Observable observable = getService().reqDemoPost(str).map(new HttpResultFunc<String>());
        makeSubscribe(observable, observer);
    }

    public void testReqPost1(String str, Observer<String> observer) {
        Map map = new HashMap();
        map.put("str", str);
        String bodyStr = new GsonBuilder().create().toJson(map);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), bodyStr);

//        RequestBody body = jsonRequestBody(map);

        Observable observable = getService().reqDemoPost1(body).map(new HttpResultFunc<String>());
        makeSubscribe(observable, observer);
    }
}
