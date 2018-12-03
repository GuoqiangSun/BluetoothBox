package com.bazooka.bluetoothbox.cache.db;

import android.support.annotation.IntDef;

import com.bazooka.bluetoothbox.cache.db.entity.LedFlash;
import com.bazooka.bluetoothbox.cache.db.entity.LedFlashInfo;
import com.bazooka.greendao.gen.DaoSession;
import com.bazooka.greendao.gen.LedFlashDao;
import com.bazooka.greendao.gen.LedFlashInfoDao;
import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/11/24
 *         作用：LedFlash 操作的辅助类
 */

public class LedFlashHelper {

    private final DaoSession mDaoSession;
    private LedFlashDao mLedFlashDao;
    private LedFlashInfoDao mLedFlashInfoDao;

    private LedFlashHelper() {
        mDaoSession = DbHelper.getInstance().getDaoSession();
        mLedFlashDao = mDaoSession.getLedFlashDao();
        mLedFlashInfoDao = mDaoSession.getLedFlashInfoDao();
    }

    private static class SingleInstanceHolder {
        static final LedFlashHelper INSTANCE = new LedFlashHelper();
    }

    public static LedFlashHelper getInstance() {
        return SingleInstanceHolder.INSTANCE;
    }

    /**
     * 保存单个伤感法
     * @param entry
     * @return
     */
    public long insertLedFlash(LedFlash entry) {
        long flashId = mLedFlashDao.insertOrReplace(entry);
        for (LedFlashInfo ledFlashInfo : entry.getLedFlashInfoList()) {
            ledFlashInfo.setId(null);
            ledFlashInfo.setFlashId(flashId);
            insertLedFlashInfo(ledFlashInfo);
        }
        return flashId;
    }

    /**
     * 添加单个闪法
     *
     * @param entry 待添加闪法
     * @param sort  排序相关字段，新添加闪法默认为2
     * @return 当前闪发在数据库中的 id
     */
    public Long insertLedFlash(LedFlash entry, int sort) {
        List<LedFlash> list = mLedFlashDao.queryBuilder()
                .where(LedFlashDao.Properties.Sort.ge(sort))
                .list();
        for (LedFlash ledFlash : list) {
            ledFlash.setSort(ledFlash.getSort() + 1);
        }
        mLedFlashDao.updateInTx(list);
        return mLedFlashDao.insert(entry);
    }

    public long getLedFlashCount() {
        return mLedFlashDao.queryBuilder().count();
    }

    public void insertLedFlashInfo(LedFlashInfo entry) {
        mLedFlashInfoDao.insert(entry);
    }

    public void insertLedFlashInfos(List<LedFlashInfo> entities) {
        mLedFlashInfoDao.insertOrReplaceInTx(entities);
    }

    /**
     * 根据名称查询
     * @param name
     * @return
     */
    public LedFlash queryFlashByName(String name) {
        return mLedFlashDao.queryBuilder()
                .where(LedFlashDao.Properties.Name.eq(name))
                .unique();
    }

    /**
     * 获得所有闪法
     * @return 所有闪法
     */
    public List<LedFlash> getAllLedFlash() {
        return mLedFlashDao.queryBuilder()
                .orderAsc(LedFlashDao.Properties.Sort)
                .list();
    }

    /**
     * 根据 sort 升序，获取指定数量的闪法
     * @param size
     * @return
     */
    public List<LedFlash> getLedFlashs(int size) {
        return mLedFlashDao.queryBuilder()
                .orderAsc(LedFlashDao.Properties.Sort)
                .limit(size)
                .list();
    }

    /**
     * 获取指定闪法的闪法详情集合
     * @param flashId 指定的闪法id
     * @return 指定闪法的闪法详情集合
     */
    public List<LedFlashInfo> getLedFlashInfo(Long flashId) {
        return mLedFlashInfoDao.queryBuilder()
                .where(LedFlashInfoDao.Properties.FlashId.eq(flashId))
                .list();
    }


    /**
     * 根据闪法 Id 删除闪法详情
     * @param id 闪法id
     */
    public void deleteFlashInfoByFlashId(long id){
        mLedFlashInfoDao.queryBuilder()
                .where(LedFlashInfoDao.Properties.FlashId.eq(id))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
    }

    /**
     * 删除单个闪法，并把所有 sort 大于 当前待删除的 Sort 的值 -1
     *
     * @param removedLedFlash 待删除闪法
     */
    public void delete(LedFlash removedLedFlash) {

        mLedFlashDao.delete(removedLedFlash);
        deleteFlashInfoByFlashId(removedLedFlash.getId());
        List<LedFlash> list = mLedFlashDao.queryBuilder()
                .where(LedFlashDao.Properties.Sort.gt(removedLedFlash.getSort()))
                .list();
        for (LedFlash ledFlash : list) {
            ledFlash.setSort(ledFlash.getSort() - 1);
        }
        mLedFlashDao.updateInTx(list);
    }

    /**
     * 删除所有
     */
    public void deleteAll() {
        mLedFlashDao.deleteAll();
        mLedFlashInfoDao.deleteAll();
    }

    public void updateLedFlash(LedFlash... ledFlashes) {
        mLedFlashDao.updateInTx(ledFlashes);
    }
}
