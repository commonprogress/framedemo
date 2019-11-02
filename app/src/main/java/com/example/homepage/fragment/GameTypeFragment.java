package com.example.homepage.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.dongxl.library.mvp.BaseMvpFragment;
import com.example.homepage.contract.GameTypeContract;
import com.example.homepage.presenter.GameTypePresenter;
import com.example.myapplication.R;

/**
 * @Author: dongxl
 * @Date: 2019-05-21 15:44:12
 * @Description:
 */
public class GameTypeFragment
        extends BaseMvpFragment<GameTypeContract.View, GameTypeContract.Presenter>
        implements GameTypeContract.View {

    public static final String TAG = GameTypeFragment.class.getSimpleName();

    /**
     * ———————————————— ↓↓↓↓ BaseMvpFragment code ↓↓↓↓ ————————————————
     */
    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public GameTypeContract.Presenter createPresenter() {
        return new GameTypePresenter();
    }

    public static GameTypeFragment newInstance() {
        GameTypeFragment fragment = new GameTypeFragment();
        Bundle argument = new Bundle();
        argument.putString("name", TAG);
        fragment.setArguments(argument);
        return fragment;
    }

    /**
     * ———————————————— ↓↓↓↓ MvpView code ↓↓↓↓ ————————————————
     */

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

    }

    @Override
    public boolean isAlived() {
        return isAdded();
    }

    /**
     * ———————————————— ↓↓↓↓ Lifecycle code ↓↓↓↓ ————————————————
     */

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getResourceId() {
         return R.layout.fragment_gametype;
    }

    /**
     * ———————————————— ↓↓↓↓ GameTypeFragment.View code ↓↓↓↓ ————————————————
     */

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}