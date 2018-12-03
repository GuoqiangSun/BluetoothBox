package com.bazooka.bluetoothbox.service.bind;

import com.bazooka.bluetoothbox.cache.db.entity.LedFlash;

import java.util.List;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2018/1/2
 *         作用：
 */

public interface IFlashSendBind {

    void send(int size);

    void stop();
}
