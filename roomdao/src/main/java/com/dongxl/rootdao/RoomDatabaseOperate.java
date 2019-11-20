package com.dongxl.rootdao;

import android.content.Context;

import com.dongxl.rootdao.daos.BookDao;
import com.dongxl.rootdao.daos.FlowDao;
import com.dongxl.rootdao.daos.UserDao;

public class RoomDatabaseOperate {
    private static volatile RoomDatabaseOperate instance;
    private RoomDatabaseManager databaseManager;

    private Context mContext;

    public static RoomDatabaseOperate getInstance() {
        if (null == instance) {
            synchronized (RoomDatabaseOperate.class) {
                if (null == instance) {
                    instance = new RoomDatabaseOperate();
                }
            }
        }
        return instance;
    }

    public RoomDatabaseOperate() {

    }

    public RoomDatabaseOperate(Context context) {
        mContext = context.getApplicationContext();
    }

    public RoomDatabaseManager getDatabaseManager(Context context) {
        if (null == databaseManager) {
            databaseManager = RoomDatabaseManager.initDatabaseCreate(context);
        }
        return databaseManager;
    }

    public UserDao getUserDao() {
        if (null == mContext) {
            return null;
        }
        return getUserDao(mContext);
    }

    public UserDao getUserDao(Context context) {
        return getDatabaseManager(context).userDao();
    }

    public BookDao getBookDao() {
        if (null == mContext) {
            return null;
        }
        return getBookDao(mContext);
    }

    public BookDao getBookDao(Context context) {
        return getDatabaseManager(context).bookDao();
    }

    public FlowDao getFlowDao() {
        if (null == mContext) {
            return null;
        }
        return getFlowDao(mContext);
    }

    public FlowDao getFlowDao(Context context) {
        return getDatabaseManager(context).flowDao();
    }

    public void getInstanceNull() {
        databaseManager = null;
        mContext = null;
        instance = null;
    }
}
