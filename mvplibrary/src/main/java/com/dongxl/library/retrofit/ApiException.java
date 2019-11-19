package com.dongxl.library.retrofit;


import com.dongxl.library.R;
import com.dongxl.library.utils.ResUtil;

/**
 * Created by liukun on 2017/4/17.
 * 服务器请求中，errorcode不为0的情况下，使用ApiException进行处理
 */
public class ApiException extends RuntimeException {

    private int mErrorCode;
    private String mErrorMsg;

    private Object data;

    public ApiException(int errorCode, String errorMsg, Object data) {
        super(errorMsg);
        this.mErrorCode = errorCode;
        this.mErrorMsg = errorMsg;
        this.data = data;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public String getErrorMsg() {
        return (mErrorMsg != null && mErrorMsg.startsWith("HTTP")) ? ResUtil.getString(R.string.a_0014) : mErrorMsg;
    }

    public Object getData() {
        return data;
    }
}
