package com.bazooka.bluetoothbox.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;

import com.bazooka.bluetoothbox.bean.event.CustomCommandEvent;
import com.bazooka.bluetoothbox.bean.event.FlashSendProgress;
import com.bazooka.bluetoothbox.cache.db.LedFlashHelper;
import com.bazooka.bluetoothbox.cache.db.SendSuccessFlashHelper;
import com.bazooka.bluetoothbox.cache.db.entity.LedFlash;
import com.bazooka.bluetoothbox.cache.db.entity.LedFlashInfo;
import com.bazooka.bluetoothbox.cache.db.entity.SendSuccessFlash;
import com.bazooka.bluetoothbox.service.bind.IFlashSendBind;
import com.bazooka.bluetoothbox.utils.ColorUtils;
import com.bazooka.bluetoothbox.utils.bluetooth.BluzManagerUtils;
import com.google.common.primitives.Bytes;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2018/1/2
 *         作用：闪法发送服务
 */

public class FlashSendService extends Service {

    /**
     * 清除 SendSuccessFlash 表中数据
     */
    private final int HANDLER_WHAT_DELETE_SUCCESS_TABLE = 0x10;
    /**
     * 发送闪法
     */
    private final int HANDLER_WHAT_SEND_FLASH = 0x11;

    private HandlerThread thread;
    private Handler mSendHandler;

    private int index = 0;
    private int sendSize = 0;
    /**
     * 最大重试次数
     */
    private final int MAX_RETRY_TIMES = 5;
    /**
     * 当前重试次数
     */
    private int mCurrRetryTime = 0;

    private int succeedNum = 0;
    private int failedNum = 0;
    private List<LedFlash> sendFlashList = new ArrayList<>();

    private boolean isSend;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new FlashSendBind();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        thread = new HandlerThread("[FlashSendService.HandlerThread]", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mSendHandler = new Handler(thread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case HANDLER_WHAT_DELETE_SUCCESS_TABLE:
                        SendSuccessFlashHelper.getInstance().deleteAll();
                        break;
                    case HANDLER_WHAT_SEND_FLASH:
                        int index = msg.arg1;
                        int size = msg.arg2;

                        if (size > 0) {
                            sendFlashList.clear();
                            sendFlashList.addAll(LedFlashHelper.getInstance().getLedFlashs(size));
                        }

                        send(sendFlashList.get(index), index);
                        break;
                    default:
                        break;
                }
            }
        };
    }


    private void send(LedFlash flash, int index) {
        Logger.d("send  index ==> " + index);
        sendFlash(flash, index);
        mSendHandler.postDelayed(() -> BluzManagerUtils.getInstance().queryFlashState(), 500);
    }

    private void sendFlash(LedFlash flash, int position) {
        List<Byte> data = new ArrayList<>();

        List<LedFlashInfo> flashInfoList = LedFlashHelper.getInstance().getLedFlashInfo(flash.getId());
        String name = toSizeString(flash.getName(), 8);
        //闪法的名称
        data.addAll(Bytes.asList(name.getBytes()));
        //闪法序号
        data.add((byte) position);
        //闪法个数
        data.add((byte) flashInfoList.size());

        //单元模式
        for (int i = 0, length = flashInfoList.size(); i < length; i++) {
            LedFlashInfo info = flashInfoList.get(i);
            //设置单元模式高4位为单元闪法序号。低4位为单元模式数量，最多暂定8个（例如 0x04(0是闪法序号，4是闪法类型)）
            String model = Integer.toHexString(info.getIndex()) + Integer.toHexString(info.getType());
            data.add((byte) Integer.parseInt(model, 16));
            //颜色1
            int[] argb1 = ColorUtils.convertARGB(info.getColor1());
            data.add((byte) argb1[1]);
            data.add((byte) argb1[2]);
            data.add((byte) argb1[3]);
            //颜色2
            int[] argb2 = ColorUtils.convertARGB(info.getColor2());
            data.add((byte) argb2[1]);
            data.add((byte) argb2[2]);
            data.add((byte) argb2[3]);

            //重复次数
            data.add((byte) info.getRepeatTime());
            //speed
            data.add((byte) info.getOnTime());
            data.add((byte) 0);
            //Bright
            data.add((byte) info.getBright());
            //offTime
            data.add((byte) info.getOffTime());
            data.add((byte) 0);
        }

        BluzManagerUtils.getInstance().sendCommand(0x12, Bytes.toArray(data));
    }

    private String toSizeString(String s, int size) {
        StringBuilder buf = new StringBuilder(s);
        for (int i = 0; i < size - s.length(); i++) {
            buf.append(" ");
        }
        return buf.toString();
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onCustomCommand(CustomCommandEvent event) {
        if (event.getWhat() == BluzManagerUtils.KEY_ANS_FLASH_STATE) {

            if (!isSend) {
                return;
            }

            //已发送的闪法
            LedFlash sentFlash = sendFlashList.get(index);
            if (index == event.getParam2()) {
                mCurrRetryTime = 0;
                //发送成功
                SendSuccessFlashHelper.getInstance().add(new SendSuccessFlash(null, sentFlash.getId(), sentFlash.getName(), event.getParam2()));
                index++;
                succeedNum++;
            } else {
                //发送失败
                if (mCurrRetryTime < MAX_RETRY_TIMES) {
                    mCurrRetryTime++;
                } else {
                    index++;
                    failedNum++;
                    mCurrRetryTime = 0;
                }
            }

            EventBus.getDefault().postSticky(new FlashSendProgress(index, sendSize, false));

            if (index == sendSize) {
                //完成
                Logger.d("succeed ==> " + succeedNum + "   failed ==> " + failedNum);
                EventBus.getDefault().postSticky(new FlashSendProgress.FlashSendFinish(succeedNum, failedNum));
                return;
            }

            Message msg = mSendHandler.obtainMessage();
            msg.what = HANDLER_WHAT_SEND_FLASH;
            msg.arg1 = index;
            msg.arg2 = 0;
            msg.sendToTarget();

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        thread.quit();
        mSendHandler.removeCallbacksAndMessages(null);
        mSendHandler = null;
        thread = null;
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private class FlashSendBind extends Binder implements IFlashSendBind {

        /**
         * 开始发送
         * @param size 发送的数量
         */
        @Override
        public void send(int size) {
            if (!EventBus.getDefault().isRegistered(FlashSendService.this)) {
                EventBus.getDefault().register(FlashSendService.this);
            }
            isSend = true;
            index = 0;
            sendSize = size;
            succeedNum = 0;
            failedNum = 0;
            mSendHandler.sendEmptyMessage(HANDLER_WHAT_DELETE_SUCCESS_TABLE);

            Message msg = mSendHandler.obtainMessage();
            msg.what = HANDLER_WHAT_SEND_FLASH;
            msg.arg1 = index;
            msg.arg2 = size;
            msg.sendToTarget();
        }

        /**
         * 停止发送
         */
        @Override
        public void stop() {
            isSend = false;
            EventBus.getDefault().unregister(FlashSendService.this);
            EventBus.getDefault().postSticky(new FlashSendProgress(index, sendSize, true));
        }
    }
}
