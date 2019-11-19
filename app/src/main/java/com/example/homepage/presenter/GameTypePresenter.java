package com.example.homepage.presenter;

import com.dongxl.library.mvp.MvpBasePresenter;
import com.dongxl.library.observer.LoadingObserver;
import com.dongxl.library.retrofit.ApiException;
import com.example.homepage.contract.GameTypeContract;
import com.example.homepage.model.TestModel;

/**
 * @author dongxl
 * @date: 2019-05-21 15:29:06
 * @Description:
 */
public class GameTypePresenter extends MvpBasePresenter<GameTypeContract.View>
        implements GameTypeContract.Presenter {

    LoadingObserver<String> testObserver;

    @Override
    public void attachView(GameTypeContract.View view) {
        super.attachView(view);
        testObserver = new LoadingObserver<String>(getMvpView(), new LoadingObserver.ObserverOnNextListener<String>() {

            @Override
            public void observerOnNext(String value) {

            }
        }, new LoadingObserver.ObserverOnErrorListener() {
            @Override
            public void observerOnError(ApiException e) {

            }
        });
    }

    public void testReqModel() {
        TestModel.getInstance().testReqGet("11", testObserver);
    }

    @Override
    public void cancelRequestOnDestroy() {
        if (null != testObserver) {
            testObserver.cancelRequest();
        }
    }
}