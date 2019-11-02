package com.dongxl.library.mvp;

import androidx.annotation.UiThread;


import java.lang.ref.WeakReference;

/**
 * Created by liukun on 2017/4/18.
 */

public abstract class MvpBasePresenter<V extends MvpView> implements MvpPresenter<V> {

    private WeakReference<V> mViewRef;


    @UiThread
    @Override
    public void attachView(V view) {
        mViewRef = new WeakReference<V>(view);
    }

    public V getMvpView(){
        return mViewRef == null ? null : mViewRef.get();
    }


    /**
     * 判断当前Presenter是否持有View
     * @return
     */
    public boolean isViewAttached(){
        return mViewRef != null && mViewRef.get() != null;
    }

    @UiThread
    @Override
    public void detachView() {
        if (mViewRef != null) {
            mViewRef.clear();
            mViewRef = null;
        }
//        destroyPresenter();
        cancelRequestOnDestroy();
    }

//    public abstract void destroyPresenter();
}
