package com.bazooka.bluetoothbox.ui.activity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.base.activity.BaseActivity;
import com.bazooka.bluetoothbox.bean.event.FlashSendProgress;
import com.bazooka.bluetoothbox.cache.DefaultFlashCache;
import com.bazooka.bluetoothbox.cache.db.LedFlashHelper;
import com.bazooka.bluetoothbox.cache.db.entity.LedFlash;
import com.bazooka.bluetoothbox.service.FlashSendService;
import com.bazooka.bluetoothbox.service.bind.IFlashSendBind;
import com.bazooka.bluetoothbox.ui.adapter.SeqListAdapterV2;
import com.bazooka.bluetoothbox.ui.dialog.LedProgressDialog;
import com.bazooka.bluetoothbox.ui.dialog.PromptDialogV2;
import com.bazooka.bluetoothbox.utils.DialogUtils;
import com.bazooka.bluetoothbox.utils.SpManager;
import com.bazooka.bluetoothbox.utils.bluetooth.BluzDeviceUtils;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/9/18
 * 作用：闪法列表页面
 */

public class SeqListActivity extends BaseActivity {

    private final int HANDLER_WHAT_SORT_UPWARD = 0x10;
    private final int HANDLER_WHAT_SORT_DOWNWARD = 0x11;
    private final int HANDLER_WHAT_SORT_DELETE = 0x12;
    private final int HANDLER_WHAT_RESET = 0x13;
    private final int HANDLER_WHAT_QUERY_ALL = 0x14;

    private final int PROMPT_DIALOG_WHAT_DELETE = 0x20;
    private final int PROMPT_DIALOG_WHAT_RESET = 0x21;

    private final LedFlash SPLIT_LINE = new LedFlash(LedFlash.ITEM_TYPE_LINE);
    private int MAX_SEND_NUM = 0;
    private int mEditPosition = 0;
    private String mDeviceAddress;

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.rv_seq_list)
    RecyclerView rvSeqList;
    @BindView(R.id.tv_reset)
    TextView tvReset;

    private List<LedFlash> seqList = new ArrayList<>();
    private SeqListAdapterV2 mAdapter;
    private LedProgressDialog mLedProgressDialog;
    private ProgressDialog mProgressDialog;
    private PromptDialogV2 mPromptDialog;

    private HandlerThread mHandlerThread;
    private Handler mSortHandler;
    private PopupWindow mEditPopup;

    private ServiceConnection mFlashServiceConnection;
    private IFlashSendBind mFlashSendBind;
    private PromptDialogV2 hintDialog;

    /**
     * 上移，下移，删除 的 Handler
     */
    private void initHandler() {
        mHandlerThread = new HandlerThread("SeqListActivity.SortThread", Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        mSortHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                int position = msg.arg1;
                switch (msg.what) {
                    //查询所有
                    case HANDLER_WHAT_QUERY_ALL:
                        seqList.clear();
                        seqList.addAll(LedFlashHelper.getInstance().getAllLedFlash());
                        //添加分割线
                        if (MAX_SEND_NUM > seqList.size()) {
                            MAX_SEND_NUM = seqList.size();
                        }
                        seqList.add(MAX_SEND_NUM, SPLIT_LINE);
                        runOnUiThread(() -> mAdapter.notifyDataSetChanged());
                        break;
                    //上移
                    case HANDLER_WHAT_SORT_UPWARD:
                        int prePosition;
                        if (seqList.get(position - 1).getItemType() == LedFlash.ITEM_TYPE_LINE) {
                            prePosition = position - 2;
                        } else {
                            prePosition = position - 1;
                        }
                        LedFlash upFlash = seqList.get(position);
                        LedFlash preFlash = seqList.get(prePosition);
                        int temp1 = upFlash.getSort();
                        upFlash.setSort(preFlash.getSort());
                        preFlash.setSort(temp1);
                        LedFlashHelper.getInstance().updateLedFlash(upFlash, preFlash);

                        Collections.swap(seqList, prePosition, position);
                        runOnUiThread(() -> mAdapter.notifyItemRangeChanged(prePosition, position));

                        break;
                    //下移
                    case HANDLER_WHAT_SORT_DOWNWARD:
                        int nextPosition;
                        if (seqList.get(position + 1).getItemType() == LedFlash.ITEM_TYPE_LINE) {
                            nextPosition = position + 2;
                        } else {
                            nextPosition = position + 1;
                        }

                        if(nextPosition >= seqList.size()) {
                            return;
                        }

                        LedFlash downFlash = seqList.get(position);
                        LedFlash nextFlash = seqList.get(nextPosition);
                        int temp2 = downFlash.getSort();
                        downFlash.setSort(nextFlash.getSort());
                        nextFlash.setSort(temp2);
                        LedFlashHelper.getInstance().updateLedFlash(nextFlash, downFlash);
                        Collections.swap(seqList, position, nextPosition);
                        runOnUiThread(() -> mAdapter.notifyItemRangeChanged(position, nextPosition));
                        break;
                    //删除
                    case HANDLER_WHAT_SORT_DELETE:
                        LedFlash removeData = seqList.remove(position);
                        LedFlashHelper.getInstance().delete(removeData);
                        if (position < MAX_SEND_NUM) {
                            MAX_SEND_NUM--;
                            SpManager.getInstance().saveMaxSendNum(MAX_SEND_NUM);

                        }
                        runOnUiThread(() -> mAdapter.notifyDataSetChanged());
                        break;
                    //重置
                    case HANDLER_WHAT_RESET:
                        LedFlashHelper.getInstance().deleteAll();
                        DefaultFlashCache cache = new DefaultFlashCache();
                        cache.clear();
                        cache.addCache();
                        cache.reset();
                        seqList.clear();
                        seqList.addAll(cache.getDefaultFlashList());
                        seqList.add(SPLIT_LINE);
                        runOnUiThread(() -> {
                            if(mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                            }
                            mAdapter.notifyDataSetChanged();
                            rvSeqList.postDelayed(() -> rvSeqList.smoothScrollToPosition(0), 100);
                        });
                        cache.clear();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    public void initData() {

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

        MAX_SEND_NUM = SpManager.getInstance().getMaxSendNum();
        mLedProgressDialog = new LedProgressDialog(mContext);

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage(getString(R.string.loading));

        mPromptDialog = new PromptDialogV2(mContext);
        mPromptDialog.setPositiveText(getString(R.string.cancel));
        mPromptDialog.setNegativeText(getString(R.string.yes));

        //设置可以拖拽 RecyclerView 的 adapter
        mAdapter = new SeqListAdapterV2(seqList);
        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(mAdapter) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
                return true;
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(rvSeqList);
        mAdapter.enableDragItem(itemTouchHelper, R.id.ll_line, true);

        //上移，下移，删除 的 Handler
        initHandler();

        initEditPopupWindow();
        bindService();
    }

    private void bindService(){
        mFlashServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mFlashSendBind = (IFlashSendBind) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        Intent serviceIntent = new Intent(this, FlashSendService.class);
        bindService(serviceIntent, mFlashServiceConnection, BIND_AUTO_CREATE);
    }

    /**
     * 实例化编辑的 PopupWindow
     */
    private void initEditPopupWindow() {
        mEditPopup = new PopupWindow(this);
        View view = LayoutInflater.from(mContext).inflate(R.layout.popup_edit, null);
        mEditPopup.setContentView(view);
        mEditPopup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mEditPopup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mEditPopup.setBackgroundDrawable(new BitmapDrawable());
        mEditPopup.setOutsideTouchable(true);
        mEditPopup.setAnimationStyle(android.R.style.Theme_Holo_Light_Dialog);
        view.setOnClickListener(v -> {
            LedFlash ledFlash = seqList.get(mEditPosition);
            LEDCustomerActivity.showActivity(mContext, ledFlash.getId(), ledFlash.getName(), mEditPosition == 0);
            mEditPopup.dismiss();
        });

    }

    @Override
    public void initView() {

        rvSeqList.setLayoutManager(new LinearLayoutManager(mContext));
        rvSeqList.setAdapter(mAdapter);


        mDeviceAddress = SpManager.getInstance().getConnectedDeviceAddress();
        setSyncText();


    }

    @Override
    protected void onStart() {
        super.onStart();
        mSortHandler.sendEmptyMessage(HANDLER_WHAT_QUERY_ALL);
//        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void addViewListener() {
        //拖拽事件
        mAdapter.setOnItemDragListener(new OnItemDragListener() {
            @Override
            public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {
                viewHolder.itemView.findViewById(R.id.v_line).setBackgroundColor(0xffff0000);
            }

            @Override
            public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {
            }

            @Override
            public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {
                viewHolder.itemView.findViewById(R.id.v_line).setBackgroundColor(0xff8c8c8c);
                if (pos == 0) {
                    Collections.swap(seqList, 0, 1);
                    mAdapter.notifyDataSetChanged();
                    MAX_SEND_NUM = 1;
                } else if (pos > 30) {
                    Collections.swap(seqList, 30, pos);
                    mAdapter.notifyDataSetChanged();
                    MAX_SEND_NUM = 30;
                } else {
                    MAX_SEND_NUM = pos;
                }

                setSyncText();
            }
        });

        //子控件点击事件
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.iv_remove:
                    mPromptDialog.setHintMessage(getString(R.string.delete_seq_hint));
                    mPromptDialog.show(PROMPT_DIALOG_WHAT_DELETE, position);
                    break;
                case R.id.iv_upward:
                    if (position == 1) {
                        return;
                    }
                    handleSort(HANDLER_WHAT_SORT_UPWARD, position);
                    break;
                case R.id.iv_downward:
                    if (position == seqList.size() - 1) {
                        return;
                    }
                    handleSort(HANDLER_WHAT_SORT_DOWNWARD, position);
                    break;
                case R.id.tv_seq_name:
                    mEditPosition = position;
                    mEditPopup.showAsDropDown(view, view.getWidth() / 2, -view.getHeight() * 3);
                    break;
                default:
                    break;
            }
        });

        //提示框 按钮点击事件
        mPromptDialog.setOnButtonClickListener(new PromptDialogV2.OnButtonClickListener() {
            @Override
            public void onPositiveClick() {
                mPromptDialog.dismiss();
            }

            @Override
            public void onNegativeClick() {
                switch (mPromptDialog.getWhat()) {
                    case PROMPT_DIALOG_WHAT_DELETE:
                        handleSort(HANDLER_WHAT_SORT_DELETE, mPromptDialog.getArg1());
                        break;
                    case PROMPT_DIALOG_WHAT_RESET:
                        if(!mProgressDialog.isShowing()) {
                            mProgressDialog.show();
                        }
                        Message msg = mSortHandler.obtainMessage();
                        msg.what = HANDLER_WHAT_RESET;
                        msg.sendToTarget();
                        break;
                    default:
                        break;
                }
                mPromptDialog.dismiss();
            }
        });

        mLedProgressDialog.setOnButtonClickListener(new LedProgressDialog.OnButtonClickListener() {
            @Override
            public void onCancelClick() {
                mLedProgressDialog.dismiss();
                mFlashSendBind.stop();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_seq_list;
    }


    @OnClick({R.id.iv_back, R.id.tv_sync, R.id.tv_reset, R.id.tv_upgrade})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_sync:
                if(BluzDeviceUtils.getInstance().getConnectionDevice() == null) {
                    hintDialog.show();
                    return;
                }
                if (!mLedProgressDialog.isShowing()) {
                    mLedProgressDialog.setMax(MAX_SEND_NUM);
                    mLedProgressDialog.show();
                    mLedProgressDialog.setCancelable(false);
                    mLedProgressDialog.setProgress(0);
                }
//                int size = seqList.size() <= MAX_SEND_NUM ? seqList.size() : MAX_SEND_NUM;
                mFlashSendBind.send(MAX_SEND_NUM);
                break;
            case R.id.tv_reset:
                mPromptDialog.setHintMessage(getString(R.string.seq_reset_hint));
                mPromptDialog.show(PROMPT_DIALOG_WHAT_RESET, -1);
                break;
            case R.id.tv_upgrade:
                showActivity(UpdateActivity.class);
                break;
            default:
                break;
        }
    }

    public LedProgressDialog getProgressDialog() {
        return mLedProgressDialog;
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onFlashSendProgressChanged(FlashSendProgress event) {
        int progress = event.getProgress();
        int total = event.getTotal();
        if (mLedProgressDialog.isShowing()) {
            mLedProgressDialog.setProgress(progress);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onFlashSendFinished(FlashSendProgress.FlashSendFinish event) {
        if (mLedProgressDialog.isShowing()) {
            mLedProgressDialog.setSendSuccess(getString(R.string.sync_succeed, event.getSucceedNum(), event.getFailedNum()));
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandlerThread.quit();
        mSortHandler.removeCallbacksAndMessages(null);
        unbindService(mFlashServiceConnection);
        mSortHandler = null;
        mPromptDialog = null;
    }

    /**
     * 设置下方同步数量提示语句
     */
    private void setSyncText() {
        SpManager.getInstance().saveMaxSendNum(MAX_SEND_NUM);
    }

    private void handleSort(int what, int position) {
        //第一个不允许改变位置
        if (position == 0) {
            return;
        }
        Message msg = mSortHandler.obtainMessage();
        msg.what = what;
        msg.arg1 = position;
        msg.sendToTarget();
    }
}
