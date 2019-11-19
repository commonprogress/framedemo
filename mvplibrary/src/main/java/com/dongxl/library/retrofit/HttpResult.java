/*
 * Copyright (c) 2018.
 * Author：Zhao
 * Email：joeyzhao1005@gmail.com
 */

package com.dongxl.library.retrofit;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author joeyzhao
 * 封装Http请求数据
 */
public class HttpResult<T>  implements Parcelable{

    private int errcode;
    private String errmsg;
    private Object data;


    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }


    public HttpResult() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.errcode);
        dest.writeString(this.errmsg);
    }

    protected HttpResult(Parcel in) {
        this.errcode = in.readInt();
        this.errmsg = in.readString();
    }

}
