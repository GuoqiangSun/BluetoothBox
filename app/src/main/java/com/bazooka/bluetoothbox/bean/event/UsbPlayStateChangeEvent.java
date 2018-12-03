package com.bazooka.bluetoothbox.bean.event;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/12/2
 *         作用：USB模式 播放状态改变事件
 */

public class UsbPlayStateChangeEvent {

    private int state;

    public UsbPlayStateChangeEvent(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
