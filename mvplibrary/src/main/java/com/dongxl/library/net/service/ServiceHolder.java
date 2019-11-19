/*
 * Copyright (c) 2018.
 * Author：Zhao
 * Email：joeyzhao1005@gmail.com
 */

package com.dongxl.library.net.service;

import com.dongxl.library.net.retrofitutil.RetrofitCustomHolder;
import com.dongxl.library.net.retrofitutil.RetrofitDownloadHolder;
import com.dongxl.library.net.retrofitutil.RetrofitHolder;
import com.dongxl.library.net.retrofitutil.RetrofitHolderPluto;

import java.util.LinkedList;

/**
 * Created by liukun on 2017/8/19.
 */

public class ServiceHolder {

    enum ERetrofitHolderType {
        RetrofitHolder,
        RetrofitHolderPluto,
        RetrofitHolderDownload,
        RetrofitCustomHolder,
    }

    private static volatile ServiceHolder instance;

    //获取单例
    public synchronized static ServiceHolder getInstance() {
        if (instance == null) {
            synchronized (ServiceHolder.class) {
                if (instance == null) {
                    instance = new ServiceHolder();
                }
            }
        }
        return instance;
    }

    private LinkedList linkedList;

    private ServiceHolder() {
        linkedList = new LinkedList();
    }


    /**
     * @param c
     * @param <T>
     * @return
     */
    private synchronized <T> T getService(Class<T> c, ERetrofitHolderType eRetrofitHolderType) {

//        long startTime = System.currentTimeMillis();

        int index = -1;
        T t;

        for (int i = 0; i < linkedList.size(); i++) {
            Class linkedListitem = linkedList.get(i).getClass();

            if (linkedListitem.getInterfaces()[0].getName().equals(c.getName())) {
                index = i;
                break;
            }
        }


        if (index != -1) {      //如果已经初始化过该Service
            t = (T) linkedList.get(index);
            if (index != 0) {   //将该Service移到栈顶
                linkedList.remove(index);
                linkedList.push(t);
            }

        } else {
            if (linkedList.size() > 3) {
                linkedList.pollLast();
            }
            switch (eRetrofitHolderType) {
                case RetrofitHolder: {
                    t = RetrofitHolder.getRetrofitInstance().create(c);
                    break;
                }
                case RetrofitHolderPluto: {
                    t = RetrofitHolderPluto.getRetrofitInstance().create(c);
                    break;
                }
                case RetrofitHolderDownload: {
                    t = RetrofitDownloadHolder.getRetrofitInstance().create(c);
                    break;
                }
                case RetrofitCustomHolder: {
                    t = RetrofitCustomHolder.getRetrofitInstance().create(c);
                    break;
                }
                default:
                    t = RetrofitHolder.getRetrofitInstance().create(c);
            }
            linkedList.push(t);
        }

//        LogUtils.e("ServiceHolder", "manageService loadingTime=" + (System.currentTimeMillis() - startTime));

        return t;
    }

    /**
     * 获取老接口
     *
     * @param c
     * @param <T>
     * @return
     */
    public <T> T getService(Class<T> c) {
        return getService(c, ERetrofitHolderType.RetrofitHolder);
    }

    /**
     * 获取pluto接口
     *
     * @param c
     * @param <T>
     * @return
     */
    public <T> T getPlutoService(Class<T> c) {
        return getService(c, ERetrofitHolderType.RetrofitHolderPluto);
    }

    /**
     * 获取下载接口
     *
     * @param c
     * @param <T>
     * @return
     */
    public <T> T getDownloadService(Class<T> c) {
        return getService(c, ERetrofitHolderType.RetrofitHolderDownload);
    }

    /**
     * 获取自己域名接口
     *
     * @param c
     * @param <T>
     * @return
     */
    public <T> T getCustomService(Class<T> c) {
        return getService(c, ERetrofitHolderType.RetrofitCustomHolder);
    }
}
