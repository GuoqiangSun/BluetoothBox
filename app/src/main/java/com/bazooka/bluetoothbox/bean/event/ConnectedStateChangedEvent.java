package com.bazooka.bluetoothbox.bean.event;

import android.bluetooth.BluetoothDevice;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2018/1/1
 *         作用：连接状态改变事件
 */

public class ConnectedStateChangedEvent {

    /**
     * 是否连接
     */
    private boolean isConnected;
    /**
     * 连接的设备
     */
    private BluetoothDevice bluetoothDevice;

    public ConnectedStateChangedEvent(boolean isConnected, BluetoothDevice bluetoothDevice) {
        this.isConnected = isConnected;
        this.bluetoothDevice = bluetoothDevice;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }
}
