package com.bazooka.bluetoothbox.bean;

import android.bluetooth.BluetoothDevice;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/11/17
 *         作用：蓝牙设备实体类
 */
public class BluetoothDeviceBean {

    private BluetoothDevice device;
    private int state;

    public BluetoothDeviceBean() {
    }

    public BluetoothDeviceBean(BluetoothDevice device, int state) {
        this.device = device;
        this.state = state;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
