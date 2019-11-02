package com.dongxl.library.mvp;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

/**
 * Created by liukun on 2017/4/18.
 */

public abstract class BaseMvpFragment<V extends MvpView, P extends MvpPresenter<V>> extends BaseFragment
        implements MvpDelegateCallback<V, P>, MvpView {

    protected MvpDelegate<V, P> mMvpDelegate;
    protected P mPresenter;

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
//        Log.e("BaseMvpFragment", "getPresenter");
        return mPresenter;
    }

    @Override
    public void setPresenter(P presenter) {
//        Log.e("BaseMvpFragment", "getPresenter");
        mPresenter = presenter;
    }

    @Override
    public V getMvpView() {
        return (V) this;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMvpDelegate().onViewCreated();

//        Log.e("BaseMvpFragment", "BaseMvpFragment.toString()= " + this.toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getMvpDelegate().onDestroyView();
    }


    @Override
    public void showLoading() {
        if (isAdded()) {
//            showLoadingDialog(getActivity());
        }
    }

    @Override
    public void dismissLoading() {
        if (isAdded()) {
//            dismissLoadingDialog();
        }
    }

    @Override
    public void showError(String errMes) {
//        ToastUtil.showToast(getActivity(),errMes);
    }

    @Override
    public boolean isAlived() {
        return isAdded();
    }
}
