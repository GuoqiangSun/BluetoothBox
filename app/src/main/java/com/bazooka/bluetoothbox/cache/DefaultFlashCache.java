package com.bazooka.bluetoothbox.cache;

import com.bazooka.bluetoothbox.cache.db.LedFlashHelper;
import com.bazooka.bluetoothbox.cache.db.entity.LedFlash;
import com.bazooka.bluetoothbox.cache.db.entity.LedFlashInfo;
import com.bazooka.bluetoothbox.utils.GsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/11/28
 *         作用：17种默认闪法
 */

public class DefaultFlashCache {

    private List<LedFlash> flashList = new ArrayList<>();

    public List<LedFlash>  addCache(){
        //1.白灯常量
        LedFlash demoLight = new LedFlash(null, "DOME", System.currentTimeMillis(), 1);
        List<LedFlashInfo> demoInfoList = new ArrayList<>();
        demoInfoList.add(new LedFlashInfo(null, null, 5, 0, 0xffffffff, 0xffffffff, 1, 100, 100,100));
        demoLight.setLedFlashInfoList(demoInfoList);
        flashList.add(demoLight);

        //2.红蓝绿三色渐变
        LedFlash gradientLight = new LedFlash(null, "GRADIENT", System.currentTimeMillis(), 2);
        List<LedFlashInfo> gradientInfoList = new ArrayList<>();
        gradientInfoList.add(new LedFlashInfo(null, null, 3, 0, 0xffff0000, 0xff00ff00, 1, 100, 100, 100));
        gradientInfoList.add(new LedFlashInfo(null, null, 3, 1, 0xff00ff00, 0xffff0000, 1, 100, 100, 100));
        gradientInfoList.add(new LedFlashInfo(null, null, 3, 2, 0xffff0000, 0xffff0000, 1, 100, 100, 100));
        gradientLight.setLedFlashInfoList(gradientInfoList);
        flashList.add(gradientLight);

        //3.蓝渐变
        LedFlash fade1Light = new LedFlash(null, "FADE1", System.currentTimeMillis(), 3);
        List<LedFlashInfo> fade1InfoList = new ArrayList<>();
        fade1InfoList.add(new LedFlashInfo(null, null, 4, 0, 0xff0000ff, 0x00000000, 1, 100, 100, 100));
        fade1Light.setLedFlashInfoList(fade1InfoList);
        flashList.add(fade1Light);

        //4.紫渐变（红:蓝 1:1）
        LedFlash fade2Light = new LedFlash(null, "FADE2", System.currentTimeMillis(), 4);
        List<LedFlashInfo> fade2InfoList = new ArrayList<>();
        fade2InfoList.add(new LedFlashInfo(null, null, 4, 0, 0xffff00ff, 0x00000000, 1, 100, 100, 100));
        fade2Light.setLedFlashInfoList(fade2InfoList);
        flashList.add(fade2Light);

        //5.红渐变
        LedFlash fade3Light = new LedFlash(null, "FADE3", System.currentTimeMillis(), 5);
        List<LedFlashInfo> fade3InfoList = new ArrayList<>();
        fade3InfoList.add(new LedFlashInfo(null, null, 4, 0, 0xffff0000, 0x00000000, 1, 100, 100, 100));
        fade3Light.setLedFlashInfoList(fade3InfoList);
        flashList.add(fade3Light);

        //6.黄渐变（红:绿 1:1）
        LedFlash fade4Light = new LedFlash(null, "FADE4", System.currentTimeMillis(), 6);
        List<LedFlashInfo> fade4InfoList = new ArrayList<>();
        fade4InfoList.add(new LedFlashInfo(null, null, 4, 0, 0xffffff00, 0x00000000, 1, 100, 100, 100));
        fade4Light.setLedFlashInfoList(fade4InfoList);
        flashList.add(fade4Light);

        //7.绿渐变
        LedFlash fade5Light = new LedFlash(null, "FADE5", System.currentTimeMillis(), 7);
        List<LedFlashInfo> fade5InfoList = new ArrayList<>();
        fade5InfoList.add(new LedFlashInfo(null, null, 4, 0, 0xff00ff00, 0x00000000, 1, 100, 100, 100));
        fade5Light.setLedFlashInfoList(fade5InfoList);
        flashList.add(fade5Light);

        //8.白渐变
        LedFlash fade6Light = new LedFlash(null, "FADE6", System.currentTimeMillis(), 8);
        List<LedFlashInfo> fade6InfoList = new ArrayList<>();
        fade6InfoList.add(new LedFlashInfo(null, null, 4, 0, 0xffffffff, 0x00000000, 1, 100, 100, 100));
        fade6Light.setLedFlashInfoList(fade6InfoList);
        flashList.add(fade6Light);

        //9.红绿蓝顺序亮
        LedFlash change1Light = new LedFlash(null, "CHANGE1", System.currentTimeMillis(), 9);
        List<LedFlashInfo> change1InfoList = new ArrayList<>();
        change1InfoList.add(new LedFlashInfo(null, null, 5, 0, 0xffff0000, 0x00000000, 1, 100, 100, 0));
        change1InfoList.add(new LedFlashInfo(null, null, 5, 1, 0xff00ff00, 0x00000000, 1, 100, 100, 0));
        change1InfoList.add(new LedFlashInfo(null, null, 5, 2, 0xff0000ff, 0x00000000, 1, 100, 100, 0));
        change1Light.setLedFlashInfoList(change1InfoList);
        flashList.add(change1Light);

        //10.黄红紫蓝青白绿顺序亮
        LedFlash change2Light = new LedFlash(null, "CHANGE2", System.currentTimeMillis(), 10);
        List<LedFlashInfo> change2InfoList = new ArrayList<>();
        change2InfoList.add(new LedFlashInfo(null, null, 5, 0, 0xffffff00, 0x00000000, 1, 100, 100, 0));
        change2InfoList.add(new LedFlashInfo(null, null, 5, 1, 0xffff0000, 0x00000000, 1, 100, 100, 0));
        change2InfoList.add(new LedFlashInfo(null, null, 5, 2, 0xffff00ff, 0x00000000, 1, 100, 100, 0));
        change2InfoList.add(new LedFlashInfo(null, null, 5, 3, 0xff0000ff, 0x00000000, 1, 100, 100, 0));
        change2InfoList.add(new LedFlashInfo(null, null, 5, 4, 0xff0080ff, 0x00000000, 1, 100, 100, 0));
        change2InfoList.add(new LedFlashInfo(null, null, 5, 5, 0xffffffff, 0x00000000, 1, 100, 100, 0));
        change2InfoList.add(new LedFlashInfo(null, null, 5, 6, 0xff00ff00, 0x00000000, 1, 100, 100, 0));
        change2Light.setLedFlashInfoList(change2InfoList);
        flashList.add(change2Light);

        //11.红绿蓝快闪
        LedFlash flash1Light = new LedFlash(null, "FLASH1", System.currentTimeMillis(), 11);
        List<LedFlashInfo> flash1InfoList = new ArrayList<>();
        flash1InfoList.add(new LedFlashInfo(null, null, 5, 0, 0xffff0000, 0x00000000, 1, 8, 100, 40));
        flash1InfoList.add(new LedFlashInfo(null, null, 5, 1, 0xff00ff00, 0x00000000, 1, 8, 100, 40));
        flash1InfoList.add(new LedFlashInfo(null, null, 5, 2, 0xff0000ff, 0x00000000, 1, 8, 100, 40));
        flash1Light.setLedFlashInfoList(flash1InfoList);
        flashList.add(flash1Light);

        //12.蓝快闪
        LedFlash flash2Light = new LedFlash(null, "FLASH2", System.currentTimeMillis(), 12);
        List<LedFlashInfo> flash2InfoList = new ArrayList<>();
        flash2InfoList.add(new LedFlashInfo(null, null, 5, 0, 0xff0000ff, 0x00000000, 1, 8, 100, 40));
        flash2Light.setLedFlashInfoList(flash2InfoList);
        flashList.add(flash2Light);

        //13.紫快闪
        LedFlash flash3Light = new LedFlash(null, "FLASH3", System.currentTimeMillis(), 13);
        List<LedFlashInfo> flash3InfoList = new ArrayList<>();
        flash3InfoList.add(new LedFlashInfo(null, null, 5, 0, 0xffff00ff, 0x00000000, 1, 8, 100, 40));
        flash3Light.setLedFlashInfoList(flash3InfoList);
        flashList.add(flash3Light);

        //14.红快闪
        LedFlash flash4Light = new LedFlash(null, "FLASH4", System.currentTimeMillis(), 14);
        List<LedFlashInfo> flash4InfoList = new ArrayList<>();
        flash4InfoList.add(new LedFlashInfo(null, null, 5, 0, 0xffff0000, 0x00000000, 1, 8, 100, 40));
        flash4Light.setLedFlashInfoList(flash4InfoList);
        flashList.add(flash4Light);

        //15.绿快闪
        LedFlash flash5Light = new LedFlash(null, "FLASH5", System.currentTimeMillis(), 15);
        List<LedFlashInfo> flash5InfoList = new ArrayList<>();
        flash5InfoList.add(new LedFlashInfo(null, null, 5, 0, 0xff00ff00, 0x00000000, 1, 8, 100, 40));
        flash5Light.setLedFlashInfoList(flash5InfoList);
        flashList.add(flash5Light);

        //16.白快闪
        LedFlash flash6Light = new LedFlash(null, "FLASH6", System.currentTimeMillis(), 16);
        List<LedFlashInfo> flash6InfoList = new ArrayList<>();
        flash6InfoList.add(new LedFlashInfo(null, null, 5, 0, 0xffffffff, 0x00000000, 1, 8, 100, 40));
        flash6Light.setLedFlashInfoList(flash6InfoList);
        flashList.add(flash6Light);

        //17.红绿蓝快闪
        LedFlash flash7Light = new LedFlash(null, "FLASH7", System.currentTimeMillis(), 17);
        List<LedFlashInfo> flash7InfoList = new ArrayList<>();
        flash7InfoList.add(new LedFlashInfo(null, null, 5, 0, 0xffff0000, 0x00000000, 1, 8, 100, 40));
        flash7InfoList.add(new LedFlashInfo(null, null, 5, 1, 0xff00ff00, 0x00000000, 1, 8, 100, 40));
        flash7InfoList.add(new LedFlashInfo(null, null, 5, 2, 0xff0000ff, 0x00000000, 1, 8, 100, 40));
        flash7Light.setLedFlashInfoList(flash7InfoList);
        flashList.add(flash7Light);

        return flashList;
    }

    public void clear(){
        flashList.clear();
    }

    public List<LedFlash> getDefaultFlashList(){
        return flashList;
    }

    public void reset(){
        for (LedFlash ledFlash : flashList) {
            LedFlashHelper.getInstance().insertLedFlash(ledFlash);
        }
    }

    @Override
    public String toString() {
        return GsonUtils.getInstance().toJson(flashList);
    }
}
