package com.bazooka.bluetoothbox.bean.event;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/11/18
 *         作用：音量改变事件
 */

public class VolumeChangedEvent {

    private int volume;
    private boolean mute;

    public VolumeChangedEvent(int volume, boolean mute) {
        this.volume = volume;
        this.mute = mute;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }
}
