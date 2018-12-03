package com.bazooka.bluetoothbox.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.base.activity.BaseActivity;
import com.bazooka.bluetoothbox.cache.db.LedFlashHelper;
import com.bazooka.bluetoothbox.cache.db.entity.LedFlash;
import com.bazooka.bluetoothbox.cache.db.entity.LedFlashInfo;
import com.bazooka.bluetoothbox.listener.NoDoubleClickListener;
import com.bazooka.bluetoothbox.ui.adapter.LedCustomerAdapterV2;
import com.bazooka.bluetoothbox.ui.dialog.RenameDialog;
import com.bazooka.bluetoothbox.utils.ToastUtils;
import com.bazooka.bluetoothbox.utils.ViewUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/9/16
 * 作用：自定义闪法界面
 */

public class LEDCustomerActivity extends BaseActivity {

    private final int HANDLER_WHAT_QUERY = 0x10;
    private final int HANDLER_WHAT_SAVE = 0x11;
    /**
     * 选择颜色返回
     */
    private final int REQUEST_SELECT_COLOR = 0x20;
    private final int MAX_LIST_NUMBER = 8;
    /**
     * 最长名称长度
     */
    private final int MAX_NAME_LENGTH = 8;
    /**
     * 最短名称长度
     */
    private final int MIN_NAME_LENGTH = 2;

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.et_seq_name)
    TextView tvSeqName;
    @BindView(R.id.rv_led)
    RecyclerView rvLed;
    @BindView(R.id.tv_save)
    TextView tvSave;

    private LedCustomerAdapterV2 mAdapter;
    private List<LedFlashInfo> mFlashInfoList;
    private RenameDialog mRenameDialog;

    /**
     * 修改颜色的位置
     */
    private int changedColorPosition = -1;
    /**
     * 是否是修改的第一个颜色，false 为修改的是第二个颜色
     */
    private boolean isChangeColor1;
    /**
     * 默认的闪法信息，新进来此页面时会添加一个默认的闪法详情
     */
    private final LedFlashInfo DEFAULT_FLASH_INFO =
            new LedFlashInfo(null, null, 4, 0, 0xffff0000, 0xff000000, 1, 100, 100, 100);

    /**
     * 将要修改的闪法id
     */
    private long willUpdateFlashId;
    private boolean isDome;
    private String mSeqName;
    private HandlerThread saveHandlerThread;
    private Handler mHandler;


    public static void showActivity(Activity activity, long flashId, String seqName, boolean isDome) {
        Intent intent = new Intent(activity, LEDCustomerActivity.class);
        intent.putExtra("flashId", flashId);
        intent.putExtra("seqName", seqName);
        intent.putExtra("isDemo", isDome);
        activity.startActivity(intent);

    }

    @Override
    public void initData() {

        willUpdateFlashId = getIntent().getLongExtra("flashId", -1L);
        mSeqName = getIntent().getStringExtra("seqName");
        isDome = getIntent().getBooleanExtra("isDemo", false);

        initHandler();
        mFlashInfoList = new ArrayList<>();
        mAdapter = new LedCustomerAdapterV2(mFlashInfoList);
        mAdapter.setIsDemo(isDome);

        mRenameDialog = new RenameDialog(this);
        mRenameDialog.setDemandMessage(R.string.led_flash_rename_demand);
        mRenameDialog.setInputRange("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
        mRenameDialog.setInputFilters(new InputFilter[]{new InputFilter.AllCaps(),
                new InputFilter.LengthFilter(MAX_NAME_LENGTH)});


    }

    private void initHandler() {
        saveHandlerThread = new HandlerThread("LEDCustomerActivity.saveHandlerThread",
                Process.THREAD_PRIORITY_BACKGROUND);
        saveHandlerThread.start();
        mHandler = new Handler(saveHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    //查询
                    case HANDLER_WHAT_QUERY:
                        if (willUpdateFlashId != -1) {
                            mFlashInfoList.addAll(LedFlashHelper.getInstance().getLedFlashInfo(willUpdateFlashId));
                        } else {
                            mFlashInfoList.add(new LedFlashInfo(DEFAULT_FLASH_INFO));
                        }

                        runOnUiThread(() -> mAdapter.notifyDataSetChanged());
                        break;
                    //保存
                    case HANDLER_WHAT_SAVE:
                        if (mFlashInfoList.size() == 1 && mFlashInfoList.get(0).getType() == 3) {
                            LedFlashInfo temp = mFlashInfoList.get(0);
                            mFlashInfoList.add(new LedFlashInfo(temp));
                        }
                        String flashName = ViewUtils.getString(tvSeqName);
                        LedFlash existsFlash = LedFlashHelper.getInstance().queryFlashByName(flashName);
                        LedFlash ledFlash = new LedFlash(null, flashName, System.currentTimeMillis(), 2);
                        if (existsFlash != null) {
                            //当前名称已存在，修改该闪法
                            ledFlash.setId(existsFlash.getId());
                            ledFlash.setSort(existsFlash.getSort());
                            LedFlashHelper.getInstance().updateLedFlash(ledFlash);
                            LedFlashHelper.getInstance().deleteFlashInfoByFlashId(existsFlash.getId());
                            handlerFlashInfo(existsFlash.getId());
                            LedFlashHelper.getInstance().insertLedFlashInfos(mFlashInfoList);

                            runOnUiThread(() -> {
                                ToastUtils.showShortToast(R.string.seq_is_updated);
                                if (willUpdateFlashId != -1) {
                                    finish();
                                }
                            });
                        } else {
                            //当前名称不存在闪法，新建闪法
                            Long id = LedFlashHelper.getInstance().insertLedFlash(ledFlash, 2);
                            handlerFlashInfo(id);
                            LedFlashHelper.getInstance().insertLedFlashInfos(mFlashInfoList);

                            runOnUiThread(() -> {
                                ToastUtils.showShortToast(R.string.saved_as_a_new_seq);
                                if (willUpdateFlashId != -1) {
                                    finish();
                                }
                            });
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    public void initView() {
        rvLed.setLayoutManager(new LinearLayoutManager(mContext));
        rvLed.setItemAnimator(null);
        rvLed.setAdapter(mAdapter);

        mHandler.sendEmptyMessage(HANDLER_WHAT_QUERY);

        if (!TextUtils.isEmpty(mSeqName)) {
            tvSeqName.setText(mSeqName);
        }

        if (isDome) {
            tvSeqName.setEnabled(false);
        }
    }

    @Override
    public void addViewListener() {

        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            LedFlashInfo info = mFlashInfoList.get(position);
            switch (view.getId()) {
                case R.id.iv_delete:
                    if (mFlashInfoList.size() > 1) {
                        mFlashInfoList.remove(position);

                        if(position <= mFlashInfoList.size() - 1) {
                            LedFlashInfo pre = mFlashInfoList.get(position - 1);
                            LedFlashInfo next = mFlashInfoList.get(position);
                            Logger.d("pre ==> " + pre.toString());
                            Logger.d("next ==> " + next.toString());
                            if(pre.getType() == 3 && next.getType() == 3) {
                                next.setColor1(pre.getColor2());
                            }
                        }

                        mAdapter.notifyDataSetChanged();
                    }
                    break;
                case R.id.iv_copy:
                    if (mFlashInfoList.size() < MAX_LIST_NUMBER) {
                        LedFlashInfo copyInfo = new LedFlashInfo(info);
                        mFlashInfoList.add(copyInfo);
                        mAdapter.notifyDataSetChanged();
                        rvLed.postDelayed(() -> rvLed.smoothScrollToPosition(mFlashInfoList.size() - 1), 100);
                    }
                    break;
                case R.id.rb_gradient:
                    info.setType(3);
                    if(position != 0 && mFlashInfoList.get(position - 1).getType() == 3) {
                        info.setColor1(mFlashInfoList.get(position - 1).getColor2());
                    }
                    mAdapter.notifyItemChanged(position);
                    break;
                case R.id.rb_breath:
                    info.setType(4);
                    mAdapter.notifyItemChanged(position);
                    break;
                case R.id.rb_flash:
                    info.setType(5);
                    mAdapter.notifyItemChanged(position);
                    break;
                case R.id.v_color_1:
                    if(position > 0 && mFlashInfoList.get(position - 1).getType() == 3 && info.getType() == 3) {
                        return;
                    }
                    changedColorPosition = position;
                    isChangeColor1 = true;
                    SelectorColorActivity.showActivityForResult(mContext, info.getColor1(), REQUEST_SELECT_COLOR);
                    break;
                case R.id.v_color_2:
                    changedColorPosition = position;
                    isChangeColor1 = false;
                    SelectorColorActivity.showActivityForResult(mContext, info.getColor2(), REQUEST_SELECT_COLOR);
                    break;
                default:
                    break;
            }
        });

        mRenameDialog.setOnOkClickListener((index, name) -> {
            if (TextUtils.isEmpty(name)) {
                mRenameDialog.setHintMessageColor(Color.RED);
                return;
            }
            if (name.length() < MIN_NAME_LENGTH) {
                mRenameDialog.setHintMessageColor(Color.RED);
                return;
            }
            tvSeqName.setText(name);
            mRenameDialog.dismiss();
        });

        tvSave.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                save();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_led_customer;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_SELECT_COLOR:
                int color = data.getIntExtra("selectedColor", Color.RED);
                LedFlashInfo temp = mFlashInfoList.get(changedColorPosition);
                if (isChangeColor1) {
                    //修改颜色1
                    temp.setColor1(color);
                } else {
                    //修改颜色2
                    temp.setColor2(color);
                    for (int i = changedColorPosition; i < mFlashInfoList.size(); i++) {
                        if(i < mFlashInfoList.size() - 1) {
                            LedFlashInfo nextInfo = mFlashInfoList.get(i + 1);
                            if(nextInfo.getType() == 3) {
                                Logger.d("color ==> " + mFlashInfoList.get(i).getColor2());
                                nextInfo.setColor1(mFlashInfoList.get(i).getColor2());
                            } else {
                                break;
                            }
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    @OnClick({R.id.iv_back, R.id.tv_list, R.id.et_seq_name})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_list:
                showActivity(SeqListActivity.class);
                break;
            case R.id.et_seq_name:
                mRenameDialog.show(0, tvSeqName.getText().toString());
                break;
            default:
                break;
        }
    }

    private void save() {
        if (TextUtils.isEmpty(tvSeqName.getText())) {
            ToastUtils.showShortToast(R.string.led_seq_name_empty);
            return;
        }
        if (tvSeqName.getText().length() < MIN_NAME_LENGTH) {
            ToastUtils.showShortToast(R.string.led_seq_name_too_short);
            return;
        }
        mHandler.sendEmptyMessage(HANDLER_WHAT_SAVE);
    }

    /**
     * 闪法保存到数据库前，处理一下 闪法详情
     *
     * @param flashId flashId
     */
    private void handlerFlashInfo(long flashId) {
        for (int i = 0, length = mFlashInfoList.size(); i < length; i++) {
            LedFlashInfo info = mFlashInfoList.get(i);
            info.setFlashId(flashId);
            info.setIndex(i);

            if (info.getType() != 3) {
                //颜色设置为 黑色
                info.setColor2(0xff000000);
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveHandlerThread.quit();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        saveHandlerThread = null;
    }
}
