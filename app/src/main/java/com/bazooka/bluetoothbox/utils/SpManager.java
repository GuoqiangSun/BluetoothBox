package com.bazooka.bluetoothbox.utils;

import com.blankj.utilcode.util.SPUtils;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/11/22
 *         作用：
 */

public class SpManager {

    private final String SP_NAME = "bazooka.bluetoothBox.config";

    /**
     * 当前音量
     */
    private final String KEY_CURRENT_VOLUME_INT = "CURRENT_VOLUME";
    /**
     * 最大音量
     */
    private final String KEY_MAX_VOLUME_INT = "MAX_VOLUME";
    /**
     * 当前播放的音乐ID
     */
    private final String KEY_CUR_PLAY_MUSIC_ID_LONG = "CURRENT_PLAY_MUSIC_ID";
    /**
     * 音乐列表加载是否完成
     */
    private final String KEY_MUSIC_UPDATE_FINISHED = "MUSIC_UPDATE_FINISHED";
    /**
     * 当前连接的设备地址
     */
    private final String KEY_CURRENT_CONNECT_DEVICE_ADDRESS = "CURRENT_CONNECT_DEVICE_ADDRESS";
    /**
     * 开关名称
     */
    private final String KEY_SWITCH_NAME = "SWITCH_NAME_";
    /**
     * 最多推送闪法个数
     */
    private final String KEY_MAX_SEND_FLASH_NUM = "KEY_MAX_SEND_FLASH_NUM";

    private final String KEY_LED_BRIGHTNESS = "KEY_LED_BRIGHTNESS";

    private final String KEY_LED_SPEED = "KEY_LED_SPEED";

    private SPUtils spUtils = null;

    private static final class InstanceHolder {
        private static final SpManager INSTANCE = new SpManager();
    }

    private SpManager() {
        spUtils = SPUtils.getInstance(SP_NAME);
    }

    public static SpManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public long getCurrentSongId() {
        return spUtils.getLong(KEY_CUR_PLAY_MUSIC_ID_LONG, -1);
    }

    public void saveCurrentSongId(long musicId) {
        spUtils.put(KEY_CUR_PLAY_MUSIC_ID_LONG, musicId);
    }

    public void saveMusicListUpdateFinish(boolean isFinished) {
        spUtils.put(KEY_MUSIC_UPDATE_FINISHED, isFinished);
    }

    public boolean getMusicListUpdateFinish() {
        return spUtils.getBoolean(KEY_MUSIC_UPDATE_FINISHED, false);
    }

    public void saveDeviceAddress(String address) {
        spUtils.put(KEY_CURRENT_CONNECT_DEVICE_ADDRESS, address);
    }

    public String getConnectedDeviceAddress() {
        return spUtils.getString(KEY_CURRENT_CONNECT_DEVICE_ADDRESS, "");
    }

    public void saveCurrentVolume(int volume) {
        spUtils.put(KEY_CURRENT_VOLUME_INT, volume);
    }

    public int getCurrentVolume() {
        return spUtils.getInt(KEY_CURRENT_VOLUME_INT, 0);
    }

    public void saveMaxVolume(int maxVolume) {
        spUtils.put(KEY_MAX_VOLUME_INT, maxVolume);
    }

    public int getMaxVolume() {
        return spUtils.getInt(KEY_MAX_VOLUME_INT, 31);
    }

    public void saveSwitchName(int index, String name){
        spUtils.put(KEY_SWITCH_NAME + index, name);
    }

    public String getSwitchName(int index) {
        return spUtils.getString(KEY_SWITCH_NAME + index, "");
    }

    public void saveMaxSendNum(int num) {
        spUtils.put(KEY_MAX_SEND_FLASH_NUM, num);
    }

    public int getMaxSendNum(){
        return spUtils.getInt(KEY_MAX_SEND_FLASH_NUM, 30);
    }

    public void saveLedBrightness(int light) {
        spUtils.put(KEY_LED_BRIGHTNESS, light);
    }

    public void removeLedBrightness(){
        spUtils.remove(KEY_LED_BRIGHTNESS);
    }

    public int getLedBrightness(){
        return spUtils.getInt(KEY_LED_BRIGHTNESS, -1);
    }

    public void saveLedSpeed(int speed) {
        spUtils.put(KEY_LED_SPEED, speed);
    }

    public int getLedSpeed(){
        return spUtils.getInt(KEY_LED_SPEED, -1);
    }

    public void removeLedSpeed(){
        spUtils.remove(KEY_LED_SPEED);
    }
}
