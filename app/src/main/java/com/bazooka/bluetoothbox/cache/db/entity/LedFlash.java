package com.bazooka.bluetoothbox.cache.db.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToMany;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Transient;

import com.bazooka.greendao.gen.DaoSession;
import com.bazooka.greendao.gen.LedFlashInfoDao;
import com.bazooka.greendao.gen.LedFlashDao;
import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/11/24
 *         作用：闪法
 */

@Entity
public class LedFlash implements Parcelable, MultiItemEntity {

    /**
     * 闪法
     */
    public static final int ITEM_TYPE_LED_FLASH = 1;
    /**
     * 分割线
     */
    public static final int ITEM_TYPE_LINE = 2;

    @Id(autoincrement = true)
    private Long id;
    private String name;
    private long modificationTime;

    private int sort;

    @Transient
    private List<LedFlashInfo> ledFlashInfoList;
    @Transient
    private int itemType = 1;

    @Generated(hash = 1321003996)
    public LedFlash(Long id, String name, long modificationTime, int sort) {
        this.id = id;
        this.name = name;
        this.modificationTime = modificationTime;
        this.sort = sort;
    }
    @Generated(hash = 570016719)
    public LedFlash() {
    }

    public LedFlash(int itemType) {
        this.itemType = itemType;
    }

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public long getModificationTime() {
        return this.modificationTime;
    }
    public void setModificationTime(long modificationTime) {
        this.modificationTime = modificationTime;
    }
    public int getSort() {
        return this.sort;
    }
    public void setSort(int sort) {
        this.sort = sort;
    }

    public List<LedFlashInfo> getLedFlashInfoList() {
        return ledFlashInfoList;
    }

    public void setLedFlashInfoList(List<LedFlashInfo> ledFlashInfoList) {
        this.ledFlashInfoList = ledFlashInfoList;
    }

    protected LedFlash(Parcel in) {
        long readId = in.readLong();
        id = readId == -1 ? null : readId;
        name = in.readString();
        modificationTime = in.readLong();
        sort = in.readInt();
        if(ledFlashInfoList == null) {
            ledFlashInfoList = new ArrayList<>();
        }
        in.readTypedList(ledFlashInfoList, LedFlashInfo.CREATOR);
    }

    public static final Creator<LedFlash> CREATOR = new Creator<LedFlash>() {
        @Override
        public LedFlash createFromParcel(Parcel in) {
            return new LedFlash(in);
        }

        @Override
        public LedFlash[] newArray(int size) {
            return new LedFlash[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id == null ? -1 : id);
        dest.writeString(name);
        dest.writeLong(modificationTime);
        dest.writeInt(sort);
        dest.writeTypedList(ledFlashInfoList);
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
