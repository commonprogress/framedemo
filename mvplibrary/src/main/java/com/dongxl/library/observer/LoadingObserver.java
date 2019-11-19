package com.dongxl.library.observer;

import com.dongxl.library.mvp.MvpView;
import com.dongxl.library.R;
import com.dongxl.library.retrofit.ApiException;
import com.dongxl.library.utils.AppMaster;
import com.dongxl.library.utils.DeviceUtils;
import com.dongxl.library.utils.LogUtils;
import com.dongxl.library.utils.ResUtil;
import com.dongxl.library.utils.StringUtils;
import com.dongxl.library.utils.ToastUtil;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by liukun on 2017/4/6.
 */

public class LoadingObserver<T> implements Observer<T> {

    private MvpView mMvpView;
    private ObserverOnNextListener<T> mOnNextListener;
    private ObserverOnErrorListener mOnErrorListener;
    private ObserverOnLoadingListener mOnLoadingListener;

    private Disposable mDisposable;

    public LoadingObserver(MvpView mvpView, ObserverOnNextListener<T> onNextListener, ObserverOnErrorListener onErrorListener) {
        this.mMvpView = mvpView;
        this.mOnNextListener = onNextListener;
        this.mOnErrorListener = onErrorListener;
    }

    /**
     * 设置loading监听
     *
     * @param onLoadingListener
     */
    public void setOnLoadingListener(ObserverOnLoadingListener onLoadingListener) {
        mOnLoadingListener = onLoadingListener;
    }

    @Override
    public void onSubscribe(Disposable d) {
        mDisposable = d;

        if (mMvpView != null && mMvpView.isAlived()) {

            if (!DeviceUtils.isNetWorkConnected(mMvpView.getContext().getApplicationContext())) {
                String errorMsg = ResUtil.getString(mMvpView.getContext().getApplicationContext(), R.string.a_0210);

                ApiException apiException = new ApiException(-10001, errorMsg, null);
                //如果设置了监听，通过监听处理
                if (mOnErrorListener != null) {
                    mOnErrorListener.observerOnError(apiException);
                } else {
                    mMvpView.showError(ResUtil.getString(mMvpView.getContext().getApplicationContext(), R.string.a_0210));
                }

                mDisposable.dispose();
            } else {
                //显示loading
                if (mOnLoadingListener != null) {
                    mOnLoadingListener.onShowLoading();
                } else {
                    mMvpView.showLoading();
                }
            }
        }
    }

    @Override
    public void onNext(T value) {
        if (mMvpView != null && mMvpView.isAlived()) {
            if (mOnLoadingListener != null) {
                mOnLoadingListener.onDismissLoading();
            } else {
                mMvpView.dismissLoading();
            }
        }
        if (mOnNextListener != null) {
            mOnNextListener.observerOnNext(value);
        }
    }

    @Override
    public void onError(Throwable e) {

        if (mMvpView != null && mMvpView.isAlived()) {
            if (mOnLoadingListener != null) {
                mOnLoadingListener.onDismissLoading();
            } else {
                mMvpView.dismissLoading();
            }
        }

        ApiException apiException = null;

        int errorCode;
        String errorMsg = "";
        if (e instanceof NullPointerException) {
            if (mOnNextListener != null) {
                mOnNextListener.observerOnNext(null);
                return;
            }
        } else if (e instanceof SocketTimeoutException) {
            //请求超时
            if (null != AppMaster.getInstance().getAppContext()) {
                errorMsg = AppMaster.getInstance().getAppContext().getString(R.string.a_0977);
            }
            apiException = new ApiException(-10001, errorMsg, null);
        } else if (e instanceof ConnectException || e instanceof UnknownHostException) {
            //网络连接出错
            if (null != AppMaster.getInstance().getAppContext()) {
                errorMsg = AppMaster.getInstance().getAppContext().getString(R.string.a_1439);
            }
            apiException = new ApiException(-10002, errorMsg, null);
        } else if (e instanceof ApiException) {
            apiException = ((ApiException) e);
            errorCode = apiException.getErrorCode();
            errorMsg = apiException.getErrorMsg();

            switch (errorCode) {
                case -1001:
                case -1002:

//                    BroadcastCenter.getInstance().action(ActionHolder.ACTION_TOKEN_FAIL).broadcast();
                    break;

                case -1008:
//                    DataCache.getInstance().setUserStatus("1");
//                    BroadcastCenter.getInstance().action(ActionHolder.ACTION_CHECK_USER_BLACK).broadcast();


                    break;
                default:
                    break;
            }
        } else {
            LogUtils.showException(e);
            if ("product".equals(AppMaster.getInstance().getBuildType())) {
                if (null != AppMaster.getInstance().getAppContext()) {
                    errorMsg = AppMaster.getInstance().getAppContext().getString(R.string.a_0014);
                    apiException = new ApiException(-10000, errorMsg, null);
                }
            } else {
                if (null != AppMaster.getInstance().getAppContext()) {
                    ToastUtil.showToast(AppMaster.getInstance().getAppContext(), e.getMessage());
                }
                apiException = new ApiException(-10000, e.getMessage(), null);
            }
        }

        //如果设置了监听，通过监听处理
        if (apiException != null && mOnErrorListener != null) {
            mOnErrorListener.observerOnError(apiException);
        }

        /**
         * 如果设置了错误监听，通过错误监听处理，否则按照默认处理
         */
        if (mMvpView != null && mMvpView.isAlived() && mOnErrorListener == null) {
            if (!StringUtils.isEmptyOrNullStr(errorMsg)) {
                mMvpView.showError(errorMsg.startsWith("HTTP") ? ResUtil.getString(R.string.a_0014) : errorMsg);
            } else {
                mMvpView.showError(errorMsg);
            }
        }
    }

    @Override
    public void onComplete() {
        if (mMvpView != null && mMvpView.isAlived()) {
            if (mOnLoadingListener != null) {
                mOnLoadingListener.onDismissLoading();
            } else {
                mMvpView.dismissLoading();
            }
        }
    }

    public void cancelRequest() {
        if (mMvpView != null && mMvpView.isAlived()) {
            if (mOnLoadingListener != null) {
                mOnLoadingListener.onDismissLoading();
            } else {
                mMvpView.dismissLoading();
            }
        }

        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    public interface ObserverOnNextListener<T> {
        void observerOnNext(T value);
    }

    public interface ObserverOnErrorListener {
        void observerOnError(ApiException e);
    }

    public interface ObserverOnLoadingListener {
        void onShowLoading();

        void onDismissLoading();
    }
}
