package com.bazooka.bluetoothbox.cache.db.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/12/26
 *         作用：已选择的颜色
 */

@Entity
public class SelectedColor implements MultiItemEntity{

    /**
     * 颜色
     */
    public static final int ITEM_TYPE_COLOR = 1;
    /**
     * 添加图标
     */
    public static final int ITEM_TYPE_ADD = 2;

    @Id
    private Long id;

    private int color;

    @Transient
    private boolean isRemove;

    @Transient
    private int itemType = 1;

    public SelectedColor(Long id, int color, boolean isRemove, int itemType) {
        this.id = id;
        this.color = color;
        this.isRemove = isRemove;
        this.itemType = itemType;
    }

    @Generated(hash = 921982146)
    public SelectedColor(Long id, int color) {
        this.id = id;
        this.color = color;
    }

    @Generated(hash = 1009383630)
    public SelectedColor() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isRemove() {
        return isRemove;
    }

    public void setRemove(boolean remove) {
        isRemove = remove;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    @Override
    public String toString() {
        return "SelectedColor{" +
                "id=" + id +
                ", color=" + color +
                ", isRemove=" + isRemove +
                ", itemType=" + itemType +
                '}';
    }
}
