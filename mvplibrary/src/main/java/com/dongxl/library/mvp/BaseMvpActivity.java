package com.dongxl.library.mvp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by liukun on 2017/4/25.
 */

public abstract class BaseMvpActivity<V extends MvpView, P extends MvpPresenter<V>> extends BaseActivity
        implements MvpView, MvpDelegateCallback<V, P> {

    protected MvpDelegate<V, P> mMvpDelegate;
    protected P mPresenter;

    public abstract void initViews();

    @Override
    public abstract P createPresenter();

    protected MvpDelegate<V, P> getMvpDelegate() {
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
    public Context getContext() {
        return this;
    }

    @Override
    public void showError(String errMes) {
        if (!TextUtils.isEmpty(errMes)) {
//            ToastUtil.showToast(BaseMvpActivity.this, errMes);
        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {
//        dismissLoadingDialog();
    }

    @Override
    public boolean isAlived() {
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMvpDelegate().onViewCreated();
        initViews();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
        disposables.dispose();
        getMvpDelegate().onDestroyView();
    }

    public <T> DisposableObserver<T> createDisposableObserver(DisposableObserver<T> disposableObserver) {
        disposables.add(disposableObserver);
        return disposableObserver;
    }


    protected final CompositeDisposable disposables = new CompositeDisposable();

}
