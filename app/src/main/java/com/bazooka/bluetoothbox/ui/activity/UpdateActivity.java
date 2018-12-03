package com.bazooka.bluetoothbox.ui.activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.view.View;
import android.widget.Button;

import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.base.activity.BaseActivity;
import com.bazooka.bluetoothbox.bean.event.ConnectedStateChangedEvent;
import com.bazooka.bluetoothbox.bean.event.CustomCommandEvent;
import com.bazooka.bluetoothbox.ui.dialog.PromptDialogV2;
import com.bazooka.bluetoothbox.utils.CRC_XMODEM;
import com.bazooka.bluetoothbox.utils.DialogUtils;
import com.bazooka.bluetoothbox.utils.HexUtils;
import com.bazooka.bluetoothbox.utils.ToastUtils;
import com.bazooka.bluetoothbox.utils.bluetooth.BluzDeviceUtils;
import com.bazooka.bluetoothbox.utils.bluetooth.BluzManagerUtils;
import com.google.common.primitives.Bytes;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2018/1/16
 *         作用：更新界面
 */

public class UpdateActivity extends BaseActivity {

    private static final String UPDATE_FILE_NAME = "project.bin";

    private static final int RECEIVER_SOH = 0x01;
    private static final int RECEIVER_STX = 0x02;
    private static final int RECEIVER_ETO = 0x04;
    private static final int RECEIVER_ACK = 0x06;
    private static final int RECEIVER_NAK = 0x15;
    private static final int RECEIVER_C = 0x43;

    //每次更新的文件数据包的大小
    private static final int SEND_SIZE = 128;
    private static final int HEX_0xFF = 0xff;

    private static final int REQUEST_FILE_SELECT_CODE = 0x10;
    /**
     * 状态：开始升级
     */
    private static final int TYPE_START_UPDATE = 0x100;
    /**
     * 开始升级后发送的ETO
     */
    private static final int TYPE_SEND_ETO = 0x101;
    /**
     * 状态：发送第一帧数据
     */
    private static final int TYPE_SEND_FIRST_FRAME = 0x102;
    /**
     * 发送升级文件中
     */
    private static final int TYPE_SEND_UPGRADE_FILE = 0x103;
    /**
     * 升级文件数据发送完成
     */
    private static final int TYPE_SEND_UPGRADE_FILE_FINISH = 0x104;
    /**
     * 升级结束
     */
    private static final int TYPE_UPGRADE_FINISH = 0x105;

    //开始升级
    private static final int HANDLER_WHAT_START_UPDATE = 0x110;
    //升级开始后发送ETO
    private static final int HANDLER_WHAT_SEND_ETO = 0x111;
    //升级的第一帧数据
    private static final int HANDLER_WHAT_SEND_FIRST_FRAME = 0x112;
    //发送升级文件数据
    private static final int HANDLER_WHAT_SEND_UPGRADE_FILE_DATA = 0x113;
    //升级文件发送完成
    private static final int HANDLER_WHAT_UPGRADE_FILE_SEND_FINISH = 0x114;
    //升级完成
    private static final int HANDLER_WHAT_UPGRADE_FINISH = 0x115;
    /**
     * 最大重试次数
     */
    private static final int MAX_RETRY_TIME = 4;


    @BindView(R.id.btn_query)
    Button btnQuery;
    @BindView(R.id.btn_start_update)
    Button btnStartUpdate;

    private int mCurType = -1;
    //当前重试次数
    private int mCurRetryTime = 0;

    private HandlerThread mUpdateHandlerThread;
    private Handler mUpdateHandler;
    private BluzManagerUtils mBluzManagerUtils;
    private PromptDialogV2 hintDialog;
    private ProgressDialog progressDialog;

//    private File upgradeFile;
//    private RandomAccessFile mRandomAccessFile;

    //当前发送的是文件的第几包数据
    private int pkgIndex;

    private boolean isUpgradeFail = false;
    private boolean isProceedUpgrade = true;//是否可以执行升级

    private AssetFileDescriptor fileDescriptor;//升级文件
    private FileInputStream upgradeFileInputStream;//升级文件流

    byte[] upgradeDataArr = new byte[SEND_SIZE];//升级数据

    @Override
    public int getLayoutId() {
        return R.layout.activity_update;
    }

    @Override
    public void initData() {
        isRegisterEventBus = false;
        EventBus.getDefault().register(this);
        mBluzManagerUtils = BluzManagerUtils.getInstance();

        hintDialog = DialogUtils.createNoConnectedDialog(mContext, new PromptDialogV2.OnButtonClickListener() {
            @Override
            public void onPositiveClick() {
                hintDialog.dismiss();
            }

            @Override
            public void onNegativeClick() {
                MainActivity.showActivity(mContext, true);
                hintDialog.dismiss();
            }
        });

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        initHandler();
    }

    private void initHandler() {
        mUpdateHandlerThread = new HandlerThread("[UpdateActivity.UpdateHandlerThread]",
                Process.THREAD_PRIORITY_BACKGROUND);
        mUpdateHandlerThread.start();
        mUpdateHandler = new Handler(mUpdateHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case HANDLER_WHAT_START_UPDATE:
                        //发送开始升级
                        mCurType = TYPE_START_UPDATE;
                        mBluzManagerUtils.startUpdate();
                        mUpdateHandler.postDelayed(() -> {
                            mBluzManagerUtils.queryUpdateState();
                        }, 2000);
                        break;
                    case HANDLER_WHAT_SEND_ETO:
                        //发送 eto 数据
                        mCurType = TYPE_SEND_ETO;
                        mBluzManagerUtils.sendUpgradeEto();
                        mUpdateHandler.postDelayed(() -> {
                            mBluzManagerUtils.queryUpdateState();
                        }, 500);
                        break;
                    case HANDLER_WHAT_SEND_FIRST_FRAME:
                        //发送第一帧数据
                        mCurType = TYPE_SEND_FIRST_FRAME;
                        sendData(createFirstFrameData());
                        mUpdateHandler.postDelayed(() -> {
                            mBluzManagerUtils.queryUpdateState();
                        }, 2000);
                        break;
                    case HANDLER_WHAT_SEND_UPGRADE_FILE_DATA:
                        //发送更新文件数据
                        mCurType = TYPE_SEND_UPGRADE_FILE;
                        sendData(createUpgradeFileData(pkgIndex - 1));
                        mUpdateHandler.postDelayed(() -> {
                            mBluzManagerUtils.queryUpdateState();
                        }, 500);
                        break;
                    case HANDLER_WHAT_UPGRADE_FILE_SEND_FINISH:
                        //更新文件发送完成
                        mCurType = TYPE_SEND_UPGRADE_FILE_FINISH;
                        mBluzManagerUtils.sendUpgradeEto();
                        mUpdateHandler.postDelayed(() -> {
                            mBluzManagerUtils.queryUpdateState();
                        }, 500);
                        break;
                    case HANDLER_WHAT_UPGRADE_FINISH:
                        //升级最后一帧，发送升级完成消息
                        mCurType = TYPE_UPGRADE_FINISH;
                        sendData(createFinishData());
                        mUpdateHandler.postDelayed(() -> {
                            mBluzManagerUtils.queryUpdateState();
                        }, 500);
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    public void initView() {

    }

    @Override
    public void addViewListener() {

    }

    @OnClick({R.id.btn_query, R.id.btn_start_update})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_query:
                if (BluzDeviceUtils.getInstance().getConnectionDevice() == null) {
                    hintDialog.show();
                    return;
                }
                mBluzManagerUtils.queryMcuVersion();
                break;
            case R.id.btn_start_update:
                if (BluzDeviceUtils.getInstance().getConnectionDevice() == null) {
                    hintDialog.show();
                    return;
                }


                try {

                    //由于 Assets 下 bin 后缀的文件会被压缩，openFd 方法会报错，故此处将 bin 后缀修改为 jpg 后缀，使得文件不被压缩
                    fileDescriptor = getAssets().openFd("BAZ_G2MNPGRM_V1.jpg");
                    upgradeFileInputStream = fileDescriptor.createInputStream();
                    //开始升级
                    ToastUtils.showShortToast(R.string.being_upgrade);
                    progressDialog.setProgress(0);
                    progressDialog.show();

                    mUpdateHandler.sendEmptyMessage(HANDLER_WHAT_START_UPDATE);
                } catch (IOException e) {
                    e.printStackTrace();
                    showUpgradeFail();
                }

//                isUpgradeFail = false;
//                isProceedUpgrade = true;
//                pkgIndex = 0;
//                ToastUtils.showShortToast(R.string.select_upgrade_file);
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("file/*");
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                try {
//                    startActivityForResult(Intent.createChooser(intent, getString(R.string.select_upgrade_file)), REQUEST_FILE_SELECT_CODE);
//                } catch (ActivityNotFoundException ex) {
//                    // Potentially direct the user to the Market with a Dialog
//                    Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
//                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_FILE_SELECT_CODE) {
//            if (resultCode == RESULT_OK) {
//                Uri uri = data.getData();
//                String filePath = MyFileUtils.getRealFilePath(mContext, uri);
//                Logger.d(filePath);
//                File file = new File(filePath);
//                String path = file.getAbsolutePath();
//                if (path.endsWith(".bin") && file.exists()) {
//                    try {
//                        upgradeFile = file;
//                        mRandomAccessFile = new RandomAccessFile(upgradeFile, "rw");
//                        //开始升级
//                        ToastUtils.showShortToast(R.string.being_upgrade);
//                        mUpdateHandler.sendEmptyMessage(HANDLER_WHAT_START_UPDATE);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                        handlerUpdateError();
//                    }
//                } else {
//                    ToastUtils.showShortToast(R.string.upgrade_file_select_error);
//                }
//            }
//        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCustomCommand(CustomCommandEvent event) {
        if (event.getWhat() == BluzManagerUtils.KEY_ANS_QUERY_MCU_VERSION) {
            int version = event.getParam1();
            ToastUtils.showShortToast("Version:" + version);
        } else if (event.getWhat() == BluzManagerUtils.KEY_ANS_UPDATE) {
            if (!isProceedUpgrade) {
                return;
            }
            int receiver = event.getBytes()[0];
            if (event.getWhat() == BluzManagerUtils.KEY_ANS_UPDATE) {
                switch (mCurType) {
                    case TYPE_START_UPDATE:
                        //发送 ETO
                        mUpdateHandler.sendEmptyMessage(HANDLER_WHAT_SEND_ETO);
                        break;
                    case TYPE_SEND_ETO:
                        if (receiver == RECEIVER_C) {
                            //发送第一帧数据
                            pkgIndex = 0;
                            mUpdateHandler.sendEmptyMessage(HANDLER_WHAT_SEND_FIRST_FRAME);
                        } else {
                            //重新发送 ETO，直到返回结果为 RECEIVER_C
                            mUpdateHandler.sendEmptyMessage(HANDLER_WHAT_SEND_ETO);
                        }
                        break;
                    case TYPE_SEND_FIRST_FRAME:
                        if (receiver == RECEIVER_C) {
                            //开始发送更新文件数据
                            pkgIndex++;
                            mUpdateHandler.sendEmptyMessage(HANDLER_WHAT_SEND_UPGRADE_FILE_DATA);
                        } else {
                            //重新发送第一帧数据，直到返回结果为 ACK
                            mUpdateHandler.sendEmptyMessage(HANDLER_WHAT_SEND_FIRST_FRAME);
                        }
                        break;
                    case TYPE_SEND_UPGRADE_FILE:
                        if (receiver == RECEIVER_ACK) {
                            mCurRetryTime = 0;
                            isUpgradeFail = false;
                            if (fileDescriptor.getLength() <= pkgIndex * SEND_SIZE) {
                                //更新文件发送完成
                                mUpdateHandler.sendEmptyMessage(HANDLER_WHAT_UPGRADE_FILE_SEND_FINISH);
                            } else {
                                progressDialog.setProgress((int) ((pkgIndex * SEND_SIZE * 100) / fileDescriptor.getLength()));
                                //继续发送更新文件数据
                                pkgIndex++;
                                mUpdateHandler.sendEmptyMessage(HANDLER_WHAT_SEND_UPGRADE_FILE_DATA);
                            }
                        } else {
                            if (mCurRetryTime == MAX_RETRY_TIME && isUpgradeFail) {
                                //重试次数达到 MAX_RETRY_TIME， 并且下一部分数据未返回 ACK ，则升级失败
                                showUpgradeFail();
                            } else {
                                if (mCurRetryTime < MAX_RETRY_TIME) {
                                    //尝试重新发送当前文件数据
                                    mCurRetryTime++;
                                    mUpdateHandler.sendEmptyMessage(HANDLER_WHAT_SEND_UPGRADE_FILE_DATA);
                                } else {
                                    isUpgradeFail = true;
                                    if (fileDescriptor.getLength() <= pkgIndex * SEND_SIZE) {
                                        //升级文件最后一部分数据发送失败
                                        showUpgradeFail();
                                        return;
                                    }
                                    //尝试次数达到 MAX_RETRY_TIME，发送更新文件的下一部分数据
                                    pkgIndex++;
                                    mUpdateHandler.sendEmptyMessage(HANDLER_WHAT_SEND_UPGRADE_FILE_DATA);
                                }
                            }
                        }
                        break;
                    case TYPE_SEND_UPGRADE_FILE_FINISH:
                        //更新文件发送完成
                        if (receiver == RECEIVER_ACK) {
                            //发送升级结束消息
                            pkgIndex = 0;
                            mUpdateHandler.sendEmptyMessage(HANDLER_WHAT_UPGRADE_FINISH);
                        } else {
                            //继续发送更新文件发送完成的消息
                            mUpdateHandler.sendEmptyMessage(HANDLER_WHAT_UPGRADE_FILE_SEND_FINISH);
                        }
                        break;
                    case TYPE_UPGRADE_FINISH:
                        //升级结束
                        mBluzManagerUtils.quitUpgreadeMode();
                        showUpgradeFinish();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 创建第一帧数据
     */
    private List<Byte> createFirstFrameData() {
        List<Byte> data = new ArrayList<>();
        data.addAll(Bytes.asList(UPDATE_FILE_NAME.getBytes()));
        //转换文件大小为字节数组
        byte[] fileSizeBytes = HexUtils.longToByteArray(fileDescriptor.getLength());
        List<Byte> fileSizeByteList = Bytes.asList(fileSizeBytes);
        int effectiveByteIndex = fileSizeByteList.lastIndexOf((byte) 0);
        //从第一个不为0的位置开始添加文件大小
        data.addAll(fileSizeByteList.subList(effectiveByteIndex + 1, fileSizeByteList.size()));
        //不足128长度的地方补0
        if (data.size() < SEND_SIZE) {
            for (int i = 0, length = SEND_SIZE - data.size(); i < length; i++) {
                data.add((byte) 0);
            }
        }
        return data;
    }

    /**
     * 创建升级文件数据
     *
     * @param index 第几次升级
     * @return
     */
    private List<Byte> createUpgradeFileData(int index) {
        List<Byte> data = new ArrayList<>();

        try {
//            mRandomAccessFile.seek(index * SEND_SIZE);
//            int len = mRandomAccessFile.read(fileDataArray, 0, SEND_SIZE);

            int length = upgradeFileInputStream.read(upgradeDataArr);
            if (length != SEND_SIZE) {
                //长度不足 128 的位置补0
                for (int i = length; upgradeDataArr.length < SEND_SIZE; i++) {
                    upgradeDataArr[i] = 0;
                }
            }
            data.addAll(Bytes.asList(upgradeDataArr));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 创建完成升级数据
     *
     * @return
     */
    private List<Byte> createFinishData() {
        List<Byte> data = new ArrayList<>();
        byte[] finishData = new byte[SEND_SIZE];
        data.addAll(Bytes.asList(finishData));
        return data;
    }

    /**
     * 发送数据
     *
     * @param data 内容数据
     */
    private void sendData(List<Byte> data) {
        List<Byte> upgradeData = new ArrayList<>();
        upgradeData.add((byte) RECEIVER_SOH);
        int index1;
        if (pkgIndex == 0) {
            index1 = 0;
        } else if (pkgIndex % HEX_0xFF == 0) {
            index1 = HEX_0xFF;
        } else {
            index1 = pkgIndex % HEX_0xFF;
        }
        upgradeData.add((byte) index1);
        upgradeData.add((byte) (HEX_0xFF - index1));
        upgradeData.addAll(data);
        byte[] crc = CRC_XMODEM.CRC_XModem(data);
        upgradeData.add(crc[0]);
        upgradeData.add(crc[1]);
        mBluzManagerUtils.sendUpgradeCommend(Bytes.toArray(upgradeData));
    }

    /**
     * 显示发送失败
     */
    private void showUpgradeFail() {
        mCurType = -1;
        isProceedUpgrade = false;
        if (upgradeFileInputStream != null) {
            try {
                upgradeFileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                upgradeFileInputStream = null;
            }
        }

        if (fileDescriptor != null) {
            try {
                fileDescriptor.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                fileDescriptor = null;
            }
        }

        ToastUtils.showShortToast(R.string.upgrade_fail);
        mUpdateHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 升级完成
     */
    private void showUpgradeFinish() {
        mCurType = -1;
//        upgradeFile = null;
//        if(mRandomAccessFile != null) {
//            try {
//                mRandomAccessFile.close();
//            } catch (IOException ignored) {
//            }
//            mRandomAccessFile = null;
//        }
        progressDialog.setProgress(100);
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (upgradeFileInputStream != null) {
            try {
                upgradeFileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                upgradeFileInputStream = null;
            }
        }

        if (fileDescriptor != null) {
            try {
                fileDescriptor.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                fileDescriptor = null;
            }
        }
        ToastUtils.showShortToast(R.string.upgrade_success);
        mUpdateHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 处理升级出错
     */
    private void handlerUpdateError() {
        mCurType = -1;
        isProceedUpgrade = false;
//        upgradeFile = null;
//        if(mRandomAccessFile != null) {
//            try {
//                mRandomAccessFile.close();
//            } catch (IOException ignored) {
//            }
//            mRandomAccessFile = null;
//        }

        if (upgradeFileInputStream != null) {
            try {
                upgradeFileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                upgradeFileInputStream = null;
            }
        }

        if (fileDescriptor != null) {
            try {
                fileDescriptor.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                fileDescriptor = null;
            }
        }

        ToastUtils.showShortToast(R.string.upgrade_fail);
        mUpdateHandler.removeCallbacksAndMessages(null);
    }

    @Override
    @SuppressWarnings({"unused"})
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionStateChanged(ConnectedStateChangedEvent event) {
        BluetoothDevice device = event.getBluetoothDevice();
        if (event.isConnected()) {

        } else {
            showBluetoothDisconnectDialog();
            if (mCurType != -1) {
                showUpgradeFail();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        mUpdateHandler.removeCallbacksAndMessages(null);
        mUpdateHandler = null;
        mUpdateHandlerThread.quit();
        mUpdateHandlerThread = null;
    }
}
