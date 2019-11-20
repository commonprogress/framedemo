package com.example.homepage.activity;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.dongxl.library.mvp.BaseMvpActivity;
import com.dongxl.rootdao.RoomDatabaseOperate;
import com.dongxl.rootdao.daos.UserDao;
import com.dongxl.rootdao.entities.UserBean;
import com.example.myapplication.R;
import com.example.homepage.contract.GameTypeContract;
import com.example.homepage.presenter.GameTypePresenter;

import java.util.List;

/**
 * @Author: dongxl
 * @Date: 2019-05-21 15:29:06
 * @Description:
 */
public class GameTypeActivity extends BaseMvpActivity<GameTypeContract.View,
        GameTypeContract.Presenter> implements GameTypeContract.View {

    public static final String TAG = GameTypeActivity.class.getSimpleName();

    private RoomDatabaseOperate databaseOperate;
    private UserDao userDao;

    public RoomDatabaseOperate getDatabaseOperate() {
        if (null != databaseOperate) {
            databaseOperate = new RoomDatabaseOperate(this);
        }
        return databaseOperate;
    }

    public UserDao getUserDao() {
        if (null != userDao) {
            userDao = getDatabaseOperate().getUserDao();
        }
        return userDao;
    }

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
        List<UserBean> list = getUserDao().getUserList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userDao = null;
        databaseOperate = null;
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