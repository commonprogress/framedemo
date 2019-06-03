package com.dongxl.library.utils;

import android.content.Context;

import com.dongxl.library.interfaces.IApp;

public class AppMaster implements IApp {

    private static AppMaster mInstance;

    private AppMaster() {
    }

    public static AppMaster getInstance() {
        if (mInstance == null) {
            mInstance = new AppMaster();
        }
        return mInstance;
    }

    public void setApp(IApp app) {
        this.app = app;
    }


    public IApp getApp() {
        return app;
    }

    private IApp app;

    @Override
    public Context getAppContext() {
        if (app == null) {
            return null;
        }
        return app.getAppContext();
    }

    @Override
    public String getBuildType() {
        if (app == null) {
            return null;
        }
        return app.getBuildType();
    }


    @Override
    public String getServicePlutoAddr() {
        return null;
    }

    @Override
    public String getServiceAddr() {
        return null;
    }

}
