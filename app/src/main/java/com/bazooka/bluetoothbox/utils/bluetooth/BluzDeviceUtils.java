package com.bazooka.bluetoothbox.utils.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.actions.ibluz.factory.BluzDeviceFactory;
import com.actions.ibluz.factory.IBluzDevice;
import com.bazooka.bluetoothbox.application.App;
import com.bazooka.bluetoothbox.bean.event.ConnectedStateChangedEvent;
import com.bazooka.bluetoothbox.exception.BluetoothNotSupportException;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

/**
 * @author 尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/10/23
 * 作用：
 */

public final class BluzDeviceUtils {


    private static final Object lock = new Object();
    private static BluzDeviceUtils instance;
    private static IBluzDevice iBluzDevice;

    private BluzDeviceUtils(){
    }

    public static BluzDeviceUtils getInstance(){
        synchronized (lock) {
            if (instance == null) {
                instance = new BluzDeviceUtils();
            }
            instance.initIBluzDevice();
        }

        return instance;
    }



    private void initIBluzDevice(){
        if(iBluzDevice == null) {
            iBluzDevice = BluzDeviceFactory.getDevice(App.getContext());
        }
    }


    /**
     * 蓝牙是否打开
     * @return 蓝牙是否打开
     * @throws BluetoothNotSupportException 设备不支持蓝牙异常
     */
    public boolean isEnable() throws BluetoothNotSupportException{

        if(iBluzDevice == null){
            throw new BluetoothNotSupportException();
        }
        return iBluzDevice.isEnabled();
    }

    /**
     *
     */
//    public

    public IBluzDevice getBluzDevice(){
        return iBluzDevice;

    }

    public void setOnConnectionListener(){
        if(iBluzDevice == null) {
            return;
        }
        iBluzDevice.setOnConnectionListener(new IBluzDevice.OnConnectionListener() {
            @Override
            public void onConnected(BluetoothDevice bluetoothDevice) {
                Logger.d("connected");
                EventBus.getDefault().post(new ConnectedStateChangedEvent(true, bluetoothDevice));
            }

            @Override
            public void onDisconnected(BluetoothDevice bluetoothDevice) {
                Logger.d("disconnected");
                EventBus.getDefault().post(new ConnectedStateChangedEvent(false, bluetoothDevice));
            }
        });
    }

    public void setOnConnectionListener(IBluzDevice.OnConnectionListener listener){
        if(iBluzDevice == null) {
            return;
        }
        iBluzDevice.setOnConnectionListener(listener);
    }

    public void startDiscovery(){
        if(iBluzDevice == null) {
            return;
        }
        iBluzDevice.startDiscovery();
    }

    public void setOnDiscoveryListener(IBluzDevice.OnDiscoveryListener listener){
        if(iBluzDevice == null) {
            return;
        }
        iBluzDevice.setOnDiscoveryListener(listener);
    }

    public BluetoothDevice getConnectionDevice() {
        if(iBluzDevice == null) {
            return null;
        }
        return iBluzDevice.getConnectedDevice();
    }

    public BluetoothDevice getConnectedA2dpDevice(){
        if(iBluzDevice == null) {
            return null;
        }
        return iBluzDevice.getConnectedA2dpDevice();
    }

    /**
     * 断开连接
     * @param device
     */
    public void disconnect(BluetoothDevice device){
        if(iBluzDevice == null) {
            return;
        }
        iBluzDevice.disconnect(device);
    }

    /**
     * 重连
     */
    public void retry(BluetoothDevice device){
        if(iBluzDevice == null) {
            return;
        }
        iBluzDevice.retry(device);
    }

    public void release(){
        if(iBluzDevice != null) {
            iBluzDevice.release();
        }
        setOnConnectionListener(null);
        iBluzDevice = null;
    }
}
