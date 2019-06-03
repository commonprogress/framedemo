package com.dongxl.library.retrofit;

/**
 * Created by liukun on 2017/4/17.
 * 封装Http请求数据
 */
public class BaseHttpResult<T> {

    private int errcode;
    private String errmsg;
    private T data;

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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
