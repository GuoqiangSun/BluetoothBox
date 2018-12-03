package com.bazooka.bluetoothbox.exception;

/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/10/23
 * 作用：蓝牙不支持异常
 */

public class BluetoothNotSupportException extends Exception {

    public BluetoothNotSupportException() {
        super("Bluetooth is not supported on this device!");
    }
}
