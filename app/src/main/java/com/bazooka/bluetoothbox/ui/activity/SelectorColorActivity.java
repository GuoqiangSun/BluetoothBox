package com.bazooka.bluetoothbox.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.base.activity.BaseActivity;
import com.bazooka.bluetoothbox.cache.db.SelectedColorHelper;
import com.bazooka.bluetoothbox.cache.db.entity.SelectedColor;
import com.bazooka.bluetoothbox.ui.adapter.ColorsAdapterV2;
import com.bazooka.bluetoothbox.ui.dialog.AddColorDialog;
import com.bazooka.bluetoothbox.utils.ColorUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/9/18
 * 作用：颜色色块选择界面
 */

public class SelectorColorActivity extends BaseActivity {

    /**
     * 查询所有已选择的颜色
     */
    private final int HANDLER_WHAT_QUERY_ALL_COLOR = 0x10;
    /**
     * 删除颜色
     */
    private final int HANDLER_WHAT_DELETE_COLOR = 0x11;
    /**
     * 添加颜色
     */
    private final int HANDLER_WHAT_ADD_COLOR = 0x12;

    /**
     * 最大颜色数量
     */
    private final int MAX_COLOR_NUM = 19;

    @BindView(R.id.rl_root)
    RelativeLayout rlRoot;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.rv_color_selector)
    RecyclerView rvColorSelector;
    @BindView(R.id.fl_selected_color)
    FrameLayout flSelectedColor;
    @BindView(R.id.v_selected_color)
    View vSelectedColor;
    @BindView(R.id.tv_selected_color_r)
    TextView tvSelectedColorR;
    @BindView(R.id.tv_selected_color_g)
    TextView tvSelectedColorG;
    @BindView(R.id.tv_selected_color_b)
    TextView tvSelectedColorB;

    private AddColorDialog mAddColorDialog;

    private List<SelectedColor> mColors;
    private ColorsAdapterV2 mAdapter;
    private int selectedColor;

    private HandlerThread colorHandlerThread = new HandlerThread("colorHandlerThread", Process.THREAD_PRIORITY_BACKGROUND);
    private Handler mColorHandler;

    public static void showActivityForResult(Activity activity, int selectedColor, int requestCode) {
        Intent intent = new Intent(activity, SelectorColorActivity.class);
        intent.putExtra("color", selectedColor);
        activity.startActivityForResult(intent, requestCode);

    }

    private void initColorHandler() {
        colorHandlerThread.start();
        mColorHandler = new Handler(colorHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    //查询所有已有颜色
                    case HANDLER_WHAT_QUERY_ALL_COLOR:
                        mColors.clear();
                        mColors.addAll(SelectedColorHelper.getInstance().queryAll());
                        mColors.add(new SelectedColor(null, 0, false, SelectedColor.ITEM_TYPE_ADD));
                        resetRemove();
                        runOnUiThread(() -> {
                            mAdapter.notifyDataSetChanged();
                            if (selectedColor == Integer.MIN_VALUE) {
                                int defaultColor = mColors.get(0).getColor();
                                selectedColor = defaultColor;
                                vSelectedColor.setBackgroundColor(defaultColor);
                                int[] defaultColorRGB = ColorUtils.convertRGB(defaultColor);
                                setRGBText(defaultColorRGB[0], defaultColorRGB[1], defaultColorRGB[2]);
                            }
                        });
                        break;
                    //删除颜色
                    case HANDLER_WHAT_DELETE_COLOR:
                        int position = msg.arg1;
                        SelectedColor removeColor = mColors.remove(position);
                        SelectedColorHelper.getInstance().deleteById(removeColor.getId());
                        runOnUiThread(() -> {
                            mAdapter.notifyItemRemoved(position);
                        });
                        break;
                    //添加颜色
                    case HANDLER_WHAT_ADD_COLOR:
                        int color = msg.arg2;
                        SelectedColor addColor = new SelectedColor(null, color, false, SelectedColor.ITEM_TYPE_COLOR);
                        mColors.add(mColors.size() - 1, addColor);
                        SelectedColorHelper.getInstance().insert(addColor);
                        runOnUiThread(() -> {
                            Intent result = new Intent();
                            result.putExtra("selectedColor", color);
                            setResult(RESULT_OK, result);
                            finish();
                        });
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    public void initData() {
        selectedColor = getIntent().getIntExtra("color", Integer.MIN_VALUE);
        Logger.d(selectedColor);
        initColorHandler();
        mColors = new ArrayList<>();
        mAdapter = new ColorsAdapterV2(mColors);

        initColorDialog();
    }

    @Override
    public void initView() {
        rvColorSelector.setLayoutManager(new GridLayoutManager(mContext, 4));
        rvColorSelector.setAdapter(mAdapter);

        mColorHandler.sendEmptyMessage(HANDLER_WHAT_QUERY_ALL_COLOR);

        if (selectedColor != Integer.MIN_VALUE) {
            vSelectedColor.setBackgroundColor(selectedColor);
            int[] selectedColorRGB = ColorUtils.convertARGB(selectedColor);
            setRGBText(selectedColorRGB[1], selectedColorRGB[2], selectedColorRGB[3]);
        }
    }


    @Override
    public void addViewListener() {

        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.v_color:
                    SelectedColor colorItem = mColors.get(position);
                    if (colorItem.isRemove()) {
                        Message removeMsg = mColorHandler.obtainMessage();
                        removeMsg.what = HANDLER_WHAT_DELETE_COLOR;
                        removeMsg.arg1 = position;
                        removeMsg.sendToTarget();
                    } else {
                        Intent result = new Intent();
                        result.putExtra("selectedColor", colorItem.getColor());
                        setResult(RESULT_OK, result);
                        finish();
                    }
                    break;
                case R.id.iv_add_color:
                    if(mColors.size() > MAX_COLOR_NUM) {
                        return;
                    }

                    mAddColorDialog.show();

                    break;
                default:
                    break;
            }
        });

        mAdapter.setOnItemChildLongClickListener((adapter, view, position) -> {
            SelectedColor colorItem = mColors.get(position);
            if (!colorItem.isRemove()) {
                resetRemove();
                colorItem.setRemove(true);
                mAdapter.notifyDataSetChanged();
            }
            return true;
        });

        mAddColorDialog.setOnColorSelectedListener(new AddColorDialog.OnColorSelectedListener() {
            @Override
            public void onColorChange(int red, int green, int blue) {
                vSelectedColor.setBackgroundColor(ColorUtils.RGB2Color(red, green, blue));
                setRGBText(red, green, blue);
            }

            @Override
            public void onColorSelected(int red, int green, int blue) {
                Message addMsg = mColorHandler.obtainMessage();
                addMsg.what = HANDLER_WHAT_ADD_COLOR;
                addMsg.arg2 = ColorUtils.RGB2Color(red, green, blue);
                addMsg.sendToTarget();
            }
        });

        ivBack.setOnClickListener(v -> finish());

    }

    private void resetRemove() {
        for (SelectedColor mColor : mColors) {
            mColor.setRemove(false);
        }
    }

    private void initColorDialog(){
        mAddColorDialog = new AddColorDialog(mContext);
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_select_color;
    }

    private void setRGBText(int red, int green, int blue) {

        tvSelectedColorR.setText("R- " + red);
        tvSelectedColorG.setText("G- " + green);
        tvSelectedColorB.setText("B- " + blue);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mColorHandler.removeCallbacksAndMessages(null);
        colorHandlerThread.quit();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP) {
            resetRemove();
            mAdapter.notifyDataSetChanged();
        }
        return super.onTouchEvent(event);
    }
}
