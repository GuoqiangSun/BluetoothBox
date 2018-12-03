package com.bazooka.bluetoothbox.bean.event;

import java.util.Arrays;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2018/1/2
 *         作用：通过蓝牙 SDK 发送自定义命令时回调事件
 */

public class CustomCommandEvent {

    private int what;
    private int param1;
    private int param2;
    private byte[] bytes;

    public CustomCommandEvent(int what, int param1, int param2, byte[] bytes) {
        this.what = what;
        this.param1 = param1;
        this.param2 = param2;
        this.bytes = bytes;
    }

    public int getWhat() {
        return what;
    }

    public void setWhat(int what) {
        this.what = what;
    }

    public int getParam1() {
        return param1;
    }

    public void setParam1(int param1) {
        this.param1 = param1;
    }

    public int getParam2() {
        return param2;
    }

    public void setParam2(int param2) {
        this.param2 = param2;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public String toString() {
        return "CustomCommandEvent{" +
                "what=" + what +
                ", \nparam1=" + param1 +
                ", param2=" + param2 +
                ", \nbytes=" + Arrays.toString(bytes) +
                '}';
    }
}
