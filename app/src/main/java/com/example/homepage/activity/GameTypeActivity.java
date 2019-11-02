package com.example.homepage.activity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.dongxl.library.mvp.BaseMvpActivity;
import com.example.myapplication.R;
import com.example.homepage.contract.GameTypeContract;
import com.example.homepage.presenter.GameTypePresenter;

/**
 * @Author: dongxl
 * @Date: 2019-05-21 15:29:06
 * @Description:
 */
public class GameTypeActivity extends BaseMvpActivity<GameTypeContract.View,
        GameTypeContract.Presenter> implements GameTypeContract.View {

    public static final String TAG = GameTypeActivity.class.getSimpleName();

    /**
     * ———————————————— ↓↓↓↓ BaseMvpActivity code ↓↓↓↓ ————————————————
     */

    @Override
    public GameTypeContract.Presenter createPresenter() {
        return new GameTypePresenter();
    }

    /**
     * ———————————————— ↓↓↓↓ MvpView code ↓↓↓↓ ————————————————
     */

    @Override
    public void showError(String errMes) {

    }

    @Override
    public void showLoading() {
//        showLoadingDialog();
    }

    @Override
    public void dismissLoading() {
//        dismissLoadingDialog();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public boolean isAlived() {
        return !isFinishing();
    }

    /**
     * ———————————————— ↓↓↓↓ Lifecycle code ↓↓↓↓ ————————————————
     */

    @Override
    public int getResourceId() {
        return R.layout.activity_gametype;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
    * ———————————————— ↓↓↓↓ GameTypeActivity.View code ↓↓↓↓ ————————————————
    */

    @Override
    public void initViews() {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}