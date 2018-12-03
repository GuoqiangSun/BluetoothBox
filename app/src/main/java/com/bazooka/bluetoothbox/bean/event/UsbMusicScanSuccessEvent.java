package com.bazooka.bluetoothbox.bean.event;

import com.actions.ibluz.manager.BluzManagerData;

import java.util.List;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/12/5
 *         作用：USB 模式 音乐扫描完毕事件
 */

public class UsbMusicScanSuccessEvent {

    private List<BluzManagerData.PListEntry> musicLisc;

    public UsbMusicScanSuccessEvent(List<BluzManagerData.PListEntry> musicLisc) {
        this.musicLisc = musicLisc;
    }

    public List<BluzManagerData.PListEntry> getMusicLisc() {
        return musicLisc;
    }

    public void setMusicLisc(List<BluzManagerData.PListEntry> musicLisc) {
        this.musicLisc = musicLisc;
    }
}
