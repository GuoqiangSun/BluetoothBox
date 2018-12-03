package com.bazooka.bluetoothbox.bean.event;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/11/28
 *         作用：U盘播放 音乐改变时间
 */

public class UsbMusicChangeEvent {

    /**
     * 艺术家
     */
    private String artist;
    /**
     * 音乐名称
     */
    private String musicName;
    /**
     * 总时长
     */
    private long duration;

    public UsbMusicChangeEvent() {
    }

    public UsbMusicChangeEvent(String artist, String musicName, long duration) {
        this.artist = artist;
        this.musicName = musicName;
        this.duration = duration;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
