package com.dongxl.library.mvp;


/**
 * Created by liukun on 2017/4/18.
 */

public interface MvpDelegateCallback<V extends MvpView, P extends MvpPresenter<V>> {

    /**
     * 创建一个presenter实例，用于MvpDelegate中绑定Presenter和View用
     * 在MvpView的实现类中实现
     * @return
     */
    P createPresenter();

    P getPresenter();

    void setPresenter(P presenter);

    V getMvpView();
}
