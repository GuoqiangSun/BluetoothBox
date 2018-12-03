package com.bazooka.bluetoothbox.cache;

import com.actions.ibluz.manager.BluzManagerData;
import com.bazooka.bluetoothbox.bean.Music;
import com.bazooka.bluetoothbox.service.PlayService;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Created by hzwangchenyan on 2016/11/23.
 * 音乐缓存
 */
public class MusicCache {
    private PlayService mPlayService;
    /**
     * 本地歌曲列表
     */
    private final List<Music> mMusicList = new ArrayList<>();

    private final List<BluzManagerData.PListEntry> usbMusicCache = new ArrayList<>();

    private MusicCache() {
    }

    private static class SingletonHolder {
        private static MusicCache sAppCache = new MusicCache();
    }

    public static MusicCache getInstance() {
        return SingletonHolder.sAppCache;
    }

    public static PlayService getPlayService() {
        return getInstance().mPlayService;
    }

    public static void setPlayService(PlayService service) {
        getInstance().mPlayService = service;
    }

    public static List<Music> getMusicList() {
        return getInstance().mMusicList;
    }


    public List<BluzManagerData.PListEntry> getUsbMusicList(){
        return usbMusicCache;
    }

}
