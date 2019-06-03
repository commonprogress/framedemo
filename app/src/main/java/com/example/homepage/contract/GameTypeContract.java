package com.example.homepage.contract;


import com.dongxl.library.mvp.MvpPresenter;
import com.dongxl.library.mvp.MvpView;

/**
 * @author dongxl
 * @date: 2019-05-21 15:29:06
 * @Description:
 */
public interface GameTypeContract {

    interface View extends MvpView {

    }

    interface Presenter extends MvpPresenter<View> {

    }
}