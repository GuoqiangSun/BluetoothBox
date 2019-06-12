package com.bazooka.bluetoothbox.utils.bluetooth;


import android.util.Log;

import com.actions.ibluz.factory.IBluzDevice;
import com.actions.ibluz.manager.BluzManager;
import com.actions.ibluz.manager.BluzManagerData;
import com.actions.ibluz.manager.IAuxManager;
import com.actions.ibluz.manager.IBluzManager;
import com.actions.ibluz.manager.IRadioManager;
import com.bazooka.bluetoothbox.BuildConfig;
import com.bazooka.bluetoothbox.application.App;
import com.bazooka.bluetoothbox.bean.event.BluzManagerReadyEvent;
import com.bazooka.bluetoothbox.bean.event.CustomCommandEvent;
import com.bazooka.bluetoothbox.bean.event.ModeChangedEvent;
import com.bazooka.bluetoothbox.bean.event.VolumeChangedEvent;
import com.bazooka.bluetoothbox.cache.MusicCache;
import com.bazooka.bluetoothbox.utils.HexUtils;
import com.bazooka.bluetoothbox.utils.SpManager;

import org.greenrobot.eventbus.EventBus;

/**
 * @author 尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/11/18
 * 作用：BluzManager 的封装工具类
 */

public class BluzManagerUtils {

    public static String TAG = "BluzManagerUtils";

    private final int KEY_LED_SET = BluzManager.buildKey(BluzManagerData.CommandType.SET, 0x81);
    //灯控状态相关
    public static final int KEY_QUE_LIGHT_CONTROL_STATE = BluzManager.buildKey(BluzManagerData.CommandType.QUE, 0x98);
    public static final int KEY_ANS_LIGHT_CONTROL_STATE = BluzManager.buildKey(BluzManagerData.CommandType.ANS, 0x98);
    //灯控闪法序号
    public static final int KEY_QUE_FLASH_STATE = BluzManager.buildKey(BluzManagerData.CommandType.QUE, 0x97);
    public static final int KEY_ANS_FLASH_STATE = BluzManager.buildKey(BluzManagerData.CommandType.ANS, 0x97);
    //查询 MCU 版本
    public static final int KEY_QUE_QUERY_MCU_VERSION = BluzManager.buildKey(BluzManagerData.CommandType.QUE, 0x9A);
    public static final int KEY_ANS_QUERY_MCU_VERSION = BluzManager.buildKey(BluzManagerData.CommandType.ANS, 0x9A);
    //升级相关
    public static final int KEY_SET_UPDATE = BluzManager.buildKey(BluzManagerData.CommandType.SET, 0x81);
    public static final int KEY_QUE_UPDATE = BluzManager.buildKey(BluzManagerData.CommandType.QUE, 0x99);
    public static final int KEY_ANS_UPDATE = BluzManager.buildKey(BluzManagerData.CommandType.ANS, 0x99);

    private BluzManager mBluzManager;

    private boolean uhostEnable = false;

    private int curMode;

    private BluzManagerUtils() {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, " KEY_LED_SET:  0x" + Integer.toHexString(KEY_LED_SET));
            Log.v(TAG, " KEY_QUE_LIGHT_CONTROL_STATE:  0x" + Integer.toHexString(KEY_QUE_LIGHT_CONTROL_STATE));
            Log.v(TAG, " KEY_ANS_LIGHT_CONTROL_STATE:  0x" + Integer.toHexString(KEY_ANS_LIGHT_CONTROL_STATE));
            Log.v(TAG, " KEY_QUE_FLASH_STATE:  0x" + Integer.toHexString(KEY_QUE_FLASH_STATE));
            Log.v(TAG, " KEY_ANS_FLASH_STATE:  0x" + Integer.toHexString(KEY_ANS_FLASH_STATE));
            Log.v(TAG, " KEY_QUE_QUERY_MCU_VERSION:  0x" + Integer.toHexString(KEY_QUE_QUERY_MCU_VERSION));
            Log.v(TAG, " KEY_ANS_QUERY_MCU_VERSION:  0x" + Integer.toHexString(KEY_ANS_QUERY_MCU_VERSION));
            Log.v(TAG, " KEY_SET_UPDATE:  0x" + Integer.toHexString(KEY_SET_UPDATE));
            Log.v(TAG, " KEY_QUE_UPDATE:  0x" + Integer.toHexString(KEY_QUE_UPDATE));
            Log.v(TAG, " KEY_ANS_UPDATE:  0x" + Integer.toHexString(KEY_ANS_UPDATE));
        }
    }

    private static final class Holder {
        final static BluzManagerUtils INSTANCE = new BluzManagerUtils();
    }

    public static BluzManagerUtils getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 创建 BluzManager
     *
     * @param device IBluzDevice
     */
    public void createBluzManager(IBluzDevice device) {
        uhostEnable = false;
        if (device != null && mBluzManager == null) {
            mBluzManager = new BluzManager(App.getContext(), device, new BluzManagerData.OnManagerReadyListener() {
                @Override
                public void onReady() {
                    mBluzManager.setSystemTime();
                    setForeground(true);
                    EventBus.getDefault().post(new BluzManagerReadyEvent());

                    mBluzManager.setOnGlobalUIChangedListener(new BluzManagerData.OnGlobalUIChangedListener() {
                        @Override
                        public void onEQChanged(int eq) {
                            Log.v(TAG, "onEQChanged:" + eq);
                        }

                        @Override
                        public void onBatteryChanged(int battery, boolean inCharge) {
                        }

                        @Override
                        public void onVolumeChanged(int volume, boolean mute) {
                            SpManager.getInstance().saveCurrentVolume(volume);
                            EventBus.getDefault().post(new VolumeChangedEvent(volume, mute));
                        }

                        @Override
                        public void onModeChanged(int mode) {
                            Log.d(TAG, "onModeChanged   mode == " + mode);
                            curMode = mode;
                            EventBus.getDefault().post(new ModeChangedEvent(mode));
                        }
                    });

                    mBluzManager.setOnHotplugChangedListener(new BluzManagerData.OnHotplugChangedListener() {
                        @Override
                        public void onCardChanged(boolean b) {

                        }

                        @Override
                        public void onUhostChanged(boolean b) {
                            uhostEnable = b;
//                            HotplugChangedEvent event = new HotplugChangedEvent();
//                            event.setUhostEnable(b);
//                            EventBus.getDefault().post(event);
                        }

                        @Override
                        public void onLineinChanged(boolean b) {

                        }

                        @Override
                        public void onUSBSoundChanged(boolean b) {

                        }
                    });

                    setOnCustomCommandListener();
                }
            });

        }
    }

    public boolean isUhostEnable() {
        return uhostEnable;
    }

    public void setOnGlobalUIChangedListener(BluzManagerData.OnGlobalUIChangedListener listener) {
        if (mBluzManager == null) {
            return;
        }
        mBluzManager.setOnGlobalUIChangedListener(listener);
    }

    public BluzManager getBluzManager() {
        return mBluzManager;
    }

    /**
     * 释放
     */
    public void release() {
        if (mBluzManager == null) {
            return;
        }
        uhostEnable = false;
        mBluzManager.release();
        mBluzManager = null;
    }


    /**
     * 设置模式
     *
     * @param mode 模式
     */
    public void setMode(int mode) {
        if (mBluzManager == null) {
            Log.e(TAG, "setMode mBluzManager == null :" + mode);
            return;
        }
        if (mode != BluzManagerData.FuncMode.A2DP && MusicCache.getPlayService() != null
                && MusicCache.getPlayService().isPlaying()) {
            MusicCache.getPlayService().playPause();
        }
        Log.v(TAG, "setMode:" + mode);
        mBluzManager.setMode(mode);
    }

    public int getCurrentMode() {
        return curMode;
    }

    public void setSystemTime() {
        if (mBluzManager == null) {
            return;
        }
        mBluzManager.setSystemTime();
    }

    public void setForeground(boolean foreground) {
        if (mBluzManager == null) {
            return;
        }
        mBluzManager.setForeground(foreground);
    }

    public IAuxManager getAuxManager(BluzManagerData.OnManagerReadyListener listener) {
        if (mBluzManager == null) {
            return null;
        }
        return mBluzManager.getAuxManager(listener);
    }

    /**
     * 设置音量
     *
     * @param volume 音量
     */
    public void setVolume(int volume) {
        if (mBluzManager == null) {
            return;
        }
        mBluzManager.setVolume(volume);
    }

    /**
     * 获取当前音量，从本地缓存获取
     *
     * @return 当前音量
     */
    public int getCurrVolume() {
        return SpManager.getInstance().getCurrentVolume();
    }

    /**
     * 获取最大音量
     *
     * @return 最大音量
     */
    public int getMaxVolume() {
        if (mBluzManager == null) {
            return 0;
        }
        return mBluzManager.getMaxVolume();
    }

    public boolean isContentChanged() {
        if (mBluzManager == null) {
            return false;
        }
        return mBluzManager.isContentChanged();
    }

    public IRadioManager getRadioManager(BluzManagerData.OnManagerReadyListener listener) {
        if (mBluzManager == null) {
            return null;
        }
        return mBluzManager.getRadioManager(listener);
    }

    public void setOnCustomCommandListener() {
        if (mBluzManager == null) {
            return;
        }
        mBluzManager.setOnCustomCommandListener((what, param1, param2, bytes) -> {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "rec Command what: 0x" + Integer.toHexString(what)
                                + " param1：0x" + Integer.toHexString(param1)
                                + "   param2: 0x" + Integer.toHexString(param2)
                                + (
                                (bytes == null)
                                        ? " 数据：null"
                                        : (" 数据：" + HexUtils.bytes2hex(bytes))
                        )
                );
            }
            EventBus.getDefault().post(new CustomCommandEvent(what, param1, param2, bytes));
        });

    }

    public void setOnCustomCommandListener(BluzManagerData.OnCustomCommandListener listener) {
        if (mBluzManager == null) {
            return;
        }
        mBluzManager.setOnCustomCommandListener(listener);
    }

    public IBluzManager getIBluzManager() {

        return mBluzManager;
    }

    /**
     * 打开、关闭 LED 灯
     */
    public void open() {
        Log.v(TAG, " open led");
        sendCommand(0x54, (byte) 0x01);
    }

    /**
     * 打开、关闭 LED 灯
     */
    public void close() {
        Log.v(TAG, " close led");
        sendCommand(0x54, (byte) 0x00);
    }

    /**
     * 打开、关闭 LED 灯
     */
    public void openOrCloseLed() {
        sendCommand(0x54);
    }

    /**
     * 发送 DEMO 闪发
     */
    public void sendDemo() {
        sendCommand(0x57);
    }

    /**
     * 发送颜色
     *
     * @param red   红
     * @param green 绿
     * @param blue  蓝
     */
    public void sendColor(int red, int green, int blue) {
        sendCommand(0x62, (byte) red, (byte) green, (byte) blue);
    }

    /**
     * 发送亮度
     *
     * @param brightness 亮度 0-255
     */
    public void sendBrightness(int brightness) {
        sendCommand(0x63, (byte) brightness);
    }

    /**
     * 发送速度
     *
     * @param speed 速度
     */
    public void sendSpeed(int speed) {
        sendCommand(0x64, (byte) speed);
    }

    public void sendFlash(int index) {
        sendCommand(0x65, (byte) index);
    }

    /**
     * 发送开关状态
     *
     * @param index 继电器 index
     * @param state 状态 1开，0关
     */
    public void sendSwitch(int index, int state) {
        //高四位为继电器下标，低四位为开关状态
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "send Switch what: 0x" + Integer.toHexString(index) + " state: 0x" + Integer.toHexString(state));
            Log.d(TAG, "send Switch 2进制==> " + Integer.toBinaryString(index) + Integer.toBinaryString(state));
        }
        String dateString = Integer.toHexString(index) + Integer.toHexString(state);
        sendCommand(0x66, (byte) Integer.parseInt(dateString, 16));

    }

    public void sendCommand(int param1, byte... data) {
        sendCustomCommand(KEY_LED_SET, param1, data.length, data);
    }

    /**
     * 查询灯控状态
     */
    public void queryLedAndLightState() {
        sendCustomCommand(KEY_QUE_LIGHT_CONTROL_STATE, 0, 0, null);
    }

    /**
     * 查询闪法是否发送成功
     */
    public void queryFlashState() {
        sendCustomCommand(KEY_QUE_FLASH_STATE, 0, 0, null);
    }

    /**
     * 查询 MCU 应用程序版本号
     */
    public void queryMcuVersion() {
        sendCustomCommand(KEY_QUE_QUERY_MCU_VERSION, 0xB5, 0, null);
    }

    /**
     * 开始升级
     */
    public void startUpdate() {
        sendCustomCommand(KEY_SET_UPDATE, 0xB1, 0, null);
    }

    /**
     * 查询升级状态
     */
    public void queryUpdateState() {
        sendCustomCommand(KEY_QUE_UPDATE, 0, 0, null);
    }

    public void sendUpgradeCommend(byte... data) {
        sendCustomCommand(KEY_SET_UPDATE, 0xB3, data.length, data);
    }

    /**
     * 升级过程中 发送 ETO
     */
    public void sendUpgradeEto() {
        sendCustomCommand(KEY_SET_UPDATE, 0xB3, 1, (byte) 0x04);
    }

    /**
     * 退出升级模式
     */
    public void quitUpgreadeMode() {
        sendCustomCommand(KEY_QUE_QUERY_MCU_VERSION, 0xB2, 0, null);
    }

    public void sendCustomCommand(int key, int param1, int param2, byte... data) {
        if (mBluzManager == null) {
            return;
        }
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "send Command cmd：0x" + Integer.toHexString(key)
                            + " param1:" + Integer.toHexString(param1)
                            + " param2:" + Integer.toHexString(param2)
                            + (
                            (data == null)
                                    ? " 数据长度：0 数据：null"
                                    : (" 数据长度：" + data.length + " 数据：" + HexUtils.bytes2hex(data))
                    )
            );
        }

        mBluzManager.sendCustomCommand(key, param1, param2, data);
    }


}
