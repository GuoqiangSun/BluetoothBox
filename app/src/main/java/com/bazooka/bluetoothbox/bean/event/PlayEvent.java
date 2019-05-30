package com.bazooka.bluetoothbox.bean.event;

/**
 * author Guoqiang_Sun
 * date 2019/5/16
 * desc
 */
public class PlayEvent {
    public PlayEvent() {
    }

    public PlayEvent(int position) {
        this.position = position;
    }

    public int position;// 当前播放的视频下标
}
