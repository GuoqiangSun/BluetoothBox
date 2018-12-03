package com.bazooka.bluetoothbox.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/11/3
 *         作用：
 */

public class GsonUtils {
    private Gson gson = null;

    private static final class InstanceHolder {
        private static final GsonUtils INSTANCE = new GsonUtils();
    }


    private GsonUtils() {
        gson = new Gson();
    }

    public static GsonUtils getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 转成json
     *
     * @param obj
     * @return String
     */
    public String toJson(Object obj) {
        return gson.toJson(obj);
    }

    /**
     * 转成bean
     *
     * @param obj
     * @param clazz
     * @return
     */
    public <T> T fromJson(Object obj, Class<T> clazz) {
        String jsonString = gson.toJson(obj);
        return gson.fromJson(jsonString, clazz);
    }

    public <T> T fromJson(String jsonString, Class<T> clazz) {
        return gson.fromJson(jsonString, clazz);
    }

    /**
     * 转成list
     *
     * @param gsonString
     * @param cls
     * @return
     */
    public <T> List<T> GsonToList(String gsonString, Class<T> cls) {
        return gson.fromJson(gsonString, new TypeToken<List<T>>() {
        }.getType());

    }

    public <T> List<T> fromJsonToArray(Object obj, Class<T> cls) {
        String jsonString = gson.toJson(obj);
        return gson.fromJson(jsonString, new TypeToken<List<T>>() {
        }.getType());
    }


    /**
     * 转成list中有map的
     *
     * @param gsonString
     * @return
     */
    public <T> List<Map<String, T>> GsonToListMaps(String gsonString) {
        List<Map<String, T>> list = null;
        list = gson.fromJson(gsonString,
                new TypeToken<List<Map<String, T>>>() {
                }.getType());
        return list;
    }

    /**
     * 转成map的
     *
     * @param gsonString
     * @return
     */
    public <T> Map<String, T> GsonToMaps(String gsonString) {
        Map<String, T> map = null;
        map = gson.fromJson(gsonString, new TypeToken<Map<String, T>>() {
        }.getType());
        return map;
    }
}