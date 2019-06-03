package com.dongxl.library.net.model;


import com.dongxl.library.net.retrofitutil.RetrofitCustomHolder;
import com.dongxl.library.net.service.CustomService;
import com.dongxl.library.net.service.ServiceHolder;

/**
 * 自定义域名model
 * Created by dongxl on 2017/11/6.
 */

public class CustomHostModel extends BaseModel {
    public static CustomHostModel instance;

    public static synchronized CustomHostModel getInstance() {
        if (instance == null) {
            instance = new CustomHostModel();
        }
        return instance;
    }

    private synchronized CustomService getService(String url) {
        RetrofitCustomHolder.setBaseUrl(url);
        return ServiceHolder.getInstance().getCustomService(CustomService.class);
    }
}
