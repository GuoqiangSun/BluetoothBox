package com.bazooka.bluetoothbox.cache.db;

import android.text.TextUtils;

import com.actions.ibluz.manager.BluzManagerData;
import com.bazooka.bluetoothbox.cache.db.entity.FmChannelCache;
import com.bazooka.greendao.gen.DaoSession;
import com.bazooka.greendao.gen.FmChannelCacheDao;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/11/22
 *         作用：FmChannelCache 操作辅助类
 */

public class FmChannelCacheHelper {

    private final DaoSession mDaoSession;
    private FmChannelCacheDao mDao;

    private FmChannelCacheHelper() {
        mDaoSession = DbHelper.getInstance().getDaoSession();
        mDao = mDaoSession.getFmChannelCacheDao();
    }

    private static class SingleInstanceHolder {
        static final FmChannelCacheHelper INSTANCE = new FmChannelCacheHelper();
    }

    public static FmChannelCacheHelper getInstance(){
        return SingleInstanceHolder.INSTANCE;
    }

    public void addCache(FmChannelCache entity){
        mDao.insert(entity);
    }
    public void deleteCache(FmChannelCache entity){
        mDao.delete(entity);
    }

    /**
     * 添加缓存
     * @param list 需要缓存的列表
     * @param needClear 是否需要清除旧数据
     * @return 返回缓存的数据
     */
    public List<FmChannelCache> addCache(List<BluzManagerData.RadioEntry> list, boolean needClear) {
        if(needClear) {
            mDao.deleteAll();
        }
        ArrayList<FmChannelCache> caches = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            BluzManagerData.RadioEntry radioEntry = list.get(i);
            if(TextUtils.isEmpty(radioEntry.name)) {
                radioEntry.name = "ST " + (i + 1);
            }
            FmChannelCache cache = new FmChannelCache();
            cache.convert(radioEntry);
            addCache(cache);
            caches.add(cache);
        }
        return caches;
    }

    /**
     * @param size 已存在的fm列表
     * @param channel 当前保存的频道
     * @return
     */
    public List<FmChannelCache> addCache(int size,int channel){
        ArrayList<FmChannelCache> caches = new ArrayList<>();
        FmChannelCache channelCache = new FmChannelCache();
        String name = "ST " + (size + 1);
        channelCache.setChannel(channel);
        channelCache.setName(name);
        addCache(channelCache); //加入数据库
        caches.add(channelCache);
        return caches;
    }

    /**
     * 查询所有缓存的频道
     * @return 所有缓存的频道
     */
    public List<FmChannelCache> getAllCacheChannel(){
        return mDao.queryBuilder().list();
    }
}
