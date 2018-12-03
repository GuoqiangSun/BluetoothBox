package com.bazooka.bluetoothbox.cache.db;

import com.bazooka.bluetoothbox.cache.db.entity.SelectedColor;
import com.bazooka.greendao.gen.DaoSession;
import com.bazooka.greendao.gen.SelectedColorDao;

import java.util.List;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/12/26
 *         作用：已选择颜色表 辅助类
 */

public class SelectedColorHelper {

    private final DaoSession mDaoSession;
    private SelectedColorDao mDao;

    private SelectedColorHelper() {
        mDaoSession = DbHelper.getInstance().getDaoSession();
        mDao = mDaoSession.getSelectedColorDao();
    }

    private static class SingleInstanceHolder {
        static final SelectedColorHelper INSTANCE = new SelectedColorHelper();
    }

    public static SelectedColorHelper getInstance() {
        return SingleInstanceHolder.INSTANCE;
    }

    public void insert(SelectedColor entry) {
        mDao.insert(entry);
    }

    public void deleteById(Long id) {
        mDao.deleteByKey(id);
    }

    public List<SelectedColor> queryAll() {
        return mDao.loadAll();
    }
}
