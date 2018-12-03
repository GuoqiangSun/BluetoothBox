package com.bazooka.bluetoothbox.bean.event;

import static android.R.attr.mode;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/11/20
 *         作用：模式改变改变事件
 */

public class ModeChangedEvent {

    /**
     * 模式
     */
    private int mode;

    public ModeChangedEvent() {
    }

    public ModeChangedEvent(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
