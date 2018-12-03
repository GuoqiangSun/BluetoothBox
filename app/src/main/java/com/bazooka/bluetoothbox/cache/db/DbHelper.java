package com.bazooka.bluetoothbox.cache.db;

import android.content.Context;

import com.bazooka.greendao.gen.DaoMaster;
import com.bazooka.greendao.gen.DaoSession;


/**
 * 数据库初始化
 * Created by whyte on 2016/10/13 0013.
 */

public class DbHelper {

    private final String DB_NAME = "bazooka.bluetoothBox.db";

    private static volatile DbHelper INSTANCE;
    private DaoMaster daoMaster;

    private DbHelper() {
    }

    public static DbHelper getInstance() {
        if (INSTANCE == null) {
            synchronized (DbHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DbHelper();
                }
            }
        }
        return INSTANCE;
    }

    public void init(Context context) {
        if (daoMaster == null) {
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
    }

    public DaoSession getDaoSession() {
        if(daoMaster == null) {
            throw new NullPointerException("dao master is not null");
        }
        return daoMaster.newSession();
    }
}
