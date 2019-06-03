package com.dongxl.library.mvp;



/**
 * Created by liukun on 2017/4/18.
 */

public class MvpDelegate<V extends MvpView, P extends MvpPresenter<V>> {


    private MvpDelegateCallback<V, P> mDelegateCallback;

    public MvpDelegate(MvpDelegateCallback<V, P> delegateCallback) {

        if (delegateCallback == null) {
            throw new NullPointerException("MvpDelegateCallback is null!");
        }

        this.mDelegateCallback = delegateCallback;
    }


    private P createaPresenter() {
        P presenter = mDelegateCallback.createPresenter();

        if (presenter == null) {
//            LogUtils.e("Presenter return from createaPresenter() is null");
        }

        return presenter;
    }

    private P getPresenter() {
        P presenter = mDelegateCallback.getPresenter();
        if (presenter == null) {
//            LogUtils.e("Presenter returned from getPresenter() is null");
        }
        return presenter;
    }

    private V getMvpView() {
        V view = mDelegateCallback.getMvpView();
        if (view == null) {
//            LogUtils.e("View returned from getMvpView() is null");
        }
        return view;
    }

    public void onViewCreated() {
        P presenter = mDelegateCallback.getPresenter();

        if (presenter == null) {
            //创建Presenter
            presenter = createaPresenter();
        }

        //用过委托将Presenter设置给View
        mDelegateCallback.setPresenter(presenter);
        //将MvpView设置给Presenter
        if (presenter != null && getMvpView() != null) {
            presenter.attachView(getMvpView());
        }

    }

    public void onDestroyView() {
        P presenter = getPresenter();
        if (presenter != null) {
            presenter.detachView();
        }
        mDelegateCallback.setPresenter(null);
    }

}
