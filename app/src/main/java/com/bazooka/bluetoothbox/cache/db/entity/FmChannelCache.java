package com.bazooka.bluetoothbox.cache.db.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.actions.ibluz.manager.BluzManagerData;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/11/22
 *         作用：Fm 频道缓存相关类
 */

@Entity
public class FmChannelCache implements Parcelable{

    @Id(autoincrement = true)
    private Long id;
    /**
     * 频道
     */
    private int channel;
    /**
     * 名称
     */
    private String name;


    public void convert(BluzManagerData.RadioEntry entry){
        this.channel = entry.channel;
        this.name = entry.name;
    }

    @Generated(hash = 781049750)
    public FmChannelCache(Long id, int channel, String name) {
        this.id = id;
        this.channel = channel;
        this.name = name;
    }

    @Generated(hash = 825968865)
    public FmChannelCache() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getChannel() {
        return this.channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected FmChannelCache(Parcel in) {
        id = in.readLong();
        channel = in.readInt();
        name = in.readString();
    }

    public static final Creator<FmChannelCache> CREATOR = new Creator<FmChannelCache>() {
        @Override
        public FmChannelCache createFromParcel(Parcel in) {
            return new FmChannelCache(in);
        }

        @Override
        public FmChannelCache[] newArray(int size) {
            return new FmChannelCache[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeInt(channel);
        dest.writeString(name);
    }
}
