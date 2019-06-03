package com.dongxl.library.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;



/**
 * Created by liukun on 2017/4/18.
 */

public abstract class MvpActivity<V extends MvpView, P extends MvpPresenter<V>> extends BaseActivity implements MvpView, MvpDelegateCallback<V, P> {

    protected MvpDelegate<V, P> mMvpDelegate;
    protected P mPresenter;

    @Override
    public abstract P createPresenter();

    protected MvpDelegate<V, P> getMvpDelegate(){
        if (mMvpDelegate == null) {
            mMvpDelegate = new MvpDelegate<>(this);
        }
        return mMvpDelegate;
    }

    @Override
    public P getPresenter() {
        return mPresenter;
    }

    @Override
    public void setPresenter(P presenter) {
        mPresenter = presenter;
    }

    @Override
    public V getMvpView() {
        return (V) this;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMvpDelegate().onViewCreated();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getMvpDelegate().onDestroyView();
    }
}
