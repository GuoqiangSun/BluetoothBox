package com.bazooka.bluetoothbox.bean.event;

import com.actions.ibluz.manager.BluzManagerData;

import java.util.List;

/**
 * @author 尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/12/5
 * 作用：USB 模式 音乐扫描完毕事件
 */

public class UsbMusicScanSuccessEvent {

    private List<BluzManagerData.PListEntry> musicLisc;

    private int select = -1;

    public void setSelect(int select) {
        this.select = select;
    }

    public int getSelect() {
        return this.select;
    }

    public UsbMusicScanSuccessEvent(List<BluzManagerData.PListEntry> musicLisc) {
        this.musicLisc = musicLisc;
    }

    public UsbMusicScanSuccessEvent(List<BluzManagerData.PListEntry> musicLisc, int select) {
        this.musicLisc = musicLisc;
        this.select = select;
    }

    public List<BluzManagerData.PListEntry> getMusicLisc() {
        return musicLisc;
    }

    public void setMusicLisc(List<BluzManagerData.PListEntry> musicLisc) {
        this.musicLisc = musicLisc;
    }
}
