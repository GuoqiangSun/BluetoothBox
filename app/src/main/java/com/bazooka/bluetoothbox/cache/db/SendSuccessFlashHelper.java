package com.bazooka.bluetoothbox.cache.db;

import com.bazooka.bluetoothbox.cache.db.entity.SendSuccessFlash;
import com.bazooka.greendao.gen.DaoSession;
import com.bazooka.greendao.gen.SendSuccessFlashDao;
import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/11/24
 *         作用：SendSuccessFlash 操作辅助类
 */

public class SendSuccessFlashHelper {

    private final DaoSession mDaoSession;
    private SendSuccessFlashDao mDao;

    private SendSuccessFlashHelper() {
        mDaoSession = DbHelper.getInstance().getDaoSession();
        mDao = mDaoSession.getSendSuccessFlashDao();
    }

    private static class SingleInstanceHolder {
        static final SendSuccessFlashHelper INSTANCE = new SendSuccessFlashHelper();
    }

    public static SendSuccessFlashHelper getInstance(){
        return SingleInstanceHolder.INSTANCE;
    }

    public void deleteAll(){
        mDao.deleteAll();
    }

    public void add(SendSuccessFlash entry){
        mDao.insert(entry);
    }

    public long getCount(){
        return mDao.queryBuilder().count();
    }

    public List<SendSuccessFlash> getAll(){
        return mDao.loadAll();
    }

}
