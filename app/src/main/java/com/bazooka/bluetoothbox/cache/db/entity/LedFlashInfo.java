package com.bazooka.bluetoothbox.cache.db.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/11/24
 *         作用：闪法详情
 */
@Entity
public class LedFlashInfo implements Parcelable {

    @Id(autoincrement = true)
    private Long id;
    /**
     * 闪法id
     */
    private Long flashId;
    /**
     * 模式 0x03:渐变，0x04:呼吸，0x05:闪烁
     */
    private int type;
    /**
     * 闪法的序号
     */
    private int index;
    private int color1;
    private int color2;
    /**
     * 重复次数
     */
    private int repeatTime;
    private int onTime;
    private int bright;
    private int offTime;

    /**
     * 第一个颜色是否可以修改
     */
    @Transient
    private boolean color1CanEdit = true;

    public LedFlashInfo(LedFlashInfo info) {
        this.flashId = info.getFlashId();
        this.type = info.getType();
        this.index = info.getIndex();
        this.color1 = info.getColor1();
        this.color2 = info.getColor2();
        this.repeatTime = info.getRepeatTime();
        this.onTime = info.getOnTime();
        this.bright = info.getBright();
        this.offTime = info.getOffTime();
    }

    @Generated(hash = 1044442135)
    public LedFlashInfo(Long id, Long flashId, int type, int index, int color1,
            int color2, int repeatTime, int onTime, int bright, int offTime) {
        this.id = id;
        this.flashId = flashId;
        this.type = type;
        this.index = index;
        this.color1 = color1;
        this.color2 = color2;
        this.repeatTime = repeatTime;
        this.onTime = onTime;
        this.bright = bright;
        this.offTime = offTime;
    }

    @Generated(hash = 847220911)
    public LedFlashInfo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFlashId() {
        return this.flashId;
    }

    public void setFlashId(Long flashId) {
        this.flashId = flashId;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getColor1() {
        return this.color1;
    }

    public void setColor1(int color1) {
        this.color1 = color1;
    }

    public int getColor2() {
        return this.color2;
    }

    public void setColor2(int color2) {
        this.color2 = color2;
    }

    public int getRepeatTime() {
        return this.repeatTime;
    }

    public void setRepeatTime(int repeatTime) {
        this.repeatTime = repeatTime;
    }

    public int getOnTime() {
        return this.onTime;
    }

    public void setOnTime(int onTime) {
        this.onTime = onTime;
    }

    public int getBright() {
        return this.bright;
    }

    public void setBright(int bright) {
        this.bright = bright;
    }

    public int getOffTime() {
        return this.offTime;
    }

    public void setOffTime(int offTime) {
        this.offTime = offTime;
    }

    public boolean isColor1CanEdit() {
        return color1CanEdit;
    }

    public void setColor1CanEdit(boolean color1CanEdit) {
        this.color1CanEdit = color1CanEdit;
    }

    protected LedFlashInfo(Parcel in) {
        long readId = in.readLong();
        id = readId == -1 ? null : readId;
        type = in.readInt();
        index = in.readInt();
        color1 = in.readInt();
        color2 = in.readInt();
        repeatTime = in.readInt();
        onTime = in.readInt();
        bright = in.readInt();
        offTime = in.readInt();
        color1CanEdit = in.readInt() == 1;
    }

    public static final Creator<LedFlashInfo> CREATOR = new Creator<LedFlashInfo>() {
        @Override
        public LedFlashInfo createFromParcel(Parcel in) {
            return new LedFlashInfo(in);
        }

        @Override
        public LedFlashInfo[] newArray(int size) {
            return new LedFlashInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id == null ? -1 : id);
        dest.writeInt(type);
        dest.writeInt(index);
        dest.writeInt(color1);
        dest.writeInt(color2);
        dest.writeInt(repeatTime);
        dest.writeInt(onTime);
        dest.writeInt(bright);
        dest.writeInt(offTime);
        dest.writeInt(color1CanEdit ? 1 : 0);
    }

    @Override
    public String toString() {
        return "LedFlashInfo{" +
                "id=" + id +
                ", flashId=" + flashId +
                ", type=" + type +
                ", index=" + index +
                ", color1=" + color1 +
                ", color2=" + color2 +
                ", repeatTime=" + repeatTime +
                ", onTime=" + onTime +
                ", bright=" + bright +
                ", offTime=" + offTime +
                ", color1CanEdit=" + color1CanEdit +
                '}';
    }
}
