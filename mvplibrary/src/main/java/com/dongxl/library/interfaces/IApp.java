package com.dongxl.library.interfaces;

import android.content.Context;

public interface IApp {
    Context getAppContext();

    String getBuildType();

    String getServicePlutoAddr();

    String getServiceAddr();

}
