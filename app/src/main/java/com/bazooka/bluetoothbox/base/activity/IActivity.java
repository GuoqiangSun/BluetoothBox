package com.bazooka.bluetoothbox.base.activity;

/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/8/31
 * 作用：每个 Activity 都要实现的方法
 */

public interface IActivity {

    /**
     * 初始化数据
     */
    void initData();

    /**
     * 初始化view
     */
    void initView();

    /**
     * 添加view监听
     */
    void addViewListener();
}
