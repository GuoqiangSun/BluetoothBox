package com.bazooka.bluetoothbox.listener;

/**
 * Created by hzwangchenyan on 2017/7/4.
 */
public interface EventCallback<T> {
    void onEvent(T t);
}
