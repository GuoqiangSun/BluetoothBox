package com.bazooka.bluetoothbox.cache.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/11/24
 *         作用：发送成功的闪法
 */
@Entity
public class SendSuccessFlash {
    @Id(autoincrement = true)
    private Long id;

    private Long flashId;
    private String flashName;
    private int index;
    @Generated(hash = 339491998)
    public SendSuccessFlash(Long id, Long flashId, String flashName, int index) {
        this.id = id;
        this.flashId = flashId;
        this.flashName = flashName;
        this.index = index;
    }
    @Generated(hash = 1815535736)
    public SendSuccessFlash() {
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
    public String getFlashName() {
        return this.flashName;
    }
    public void setFlashName(String flashName) {
        this.flashName = flashName;
    }
    public int getIndex() {
        return this.index;
    }
    public void setIndex(int index) {
        this.index = index;
    }



    
}
