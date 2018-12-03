package com.bazooka.bluetoothbox.bean.event;

import com.bazooka.bluetoothbox.bean.Music;

import java.util.List;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/12/5
 *         作用：本地音乐扫描完毕
 */

public class LocalMusicScanSuccessEvent {

    private List<Music> musicList;

    public LocalMusicScanSuccessEvent(List<Music> musicList) {
        this.musicList = musicList;
    }

    public List<Music> getMusicList() {  
        return musicList;
    }

    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
    }
}
