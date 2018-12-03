package com.bazooka.bluetoothbox.ui.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.cache.db.entity.LedFlashInfo;
import com.bazooka.bluetoothbox.ui.drawable.ProgressDrawable;
import com.bazooka.bluetoothbox.ui.view.HorizontalPickerView;
import com.bazooka.bluetoothbox.utils.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/11/24
 *         作用：自定义闪法列表适配器
 */

public class LedCustomerAdapterV2 extends BaseQuickAdapter<LedFlashInfo, BaseViewHolder> {

    private final ProgressDrawable progressDrawable = new ProgressDrawable();
    private boolean isDemo = false;

    public LedCustomerAdapterV2(@Nullable List<LedFlashInfo> data) {
        super(R.layout.item_led_customer, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, LedFlashInfo item) {
        View vColor2 = holder.getView(R.id.v_color_2);
        HorizontalPickerView pickerView = holder.getView(R.id.hpv_repeat);
        SeekBar sbOn = holder.getView(R.id.sb_on);
        SeekBar sbOff = holder.getView(R.id.sb_off);
        SeekBar sbBright = holder.getView(R.id.sb_bright);
        if (isDemo) {
            RadioButton rbGradient = holder.getView(R.id.rb_gradient);
            RadioButton rbBreath = holder.getView(R.id.rb_breath);
            RadioButton rbFlash = holder.getView(R.id.rb_flash);
            rbGradient.setEnabled(false);
            rbBreath.setEnabled(false);
            rbFlash.setEnabled(false);
            vColor2.setEnabled(false);
            pickerView.setEnabled(false);
            sbOn.setEnabled(false);
            sbOff.setEnabled(false);
            sbBright.setEnabled(false);
            holder.setVisible(R.id.iv_copy, false);
        }
        int position = holder.getLayoutPosition();
        String name = "00" + (position + 1);
        holder.setText(R.id.tv_index, name);
        holder.setBackgroundColor(R.id.v_color_1, item.getColor1());

        if (position == 0) {
            holder.setVisible(R.id.iv_delete, false);
        } else {
            holder.setVisible(R.id.iv_delete, true);
        }

        switch (item.getType()) {
            case 3:
                holder.setChecked(R.id.rb_gradient, true);
                holder.setChecked(R.id.rb_breath, false);
                holder.setChecked(R.id.rb_flash, false);
                break;
            case 4:
                holder.setChecked(R.id.rb_gradient, false);
                holder.setChecked(R.id.rb_breath, true);
                holder.setChecked(R.id.rb_flash, false);
                break;
            case 5:
                holder.setChecked(R.id.rb_gradient, false);
                holder.setChecked(R.id.rb_breath, false);
                holder.setChecked(R.id.rb_flash, true);
                break;
            default:
                break;
        }

        if (item.getType() == 3) {
            vColor2.setVisibility(View.VISIBLE);
            vColor2.setBackgroundColor(item.getColor2());
        } else {
            vColor2.setVisibility(View.INVISIBLE);
        }
        pickerView.setSelectedItem(item.getRepeatTime() - 1);
        pickerView.setOnItemSelectedListener(index -> item.setRepeatTime(index + 1));

        holder.addOnClickListener(R.id.rb_gradient);
        holder.addOnClickListener(R.id.rb_breath);
        holder.addOnClickListener(R.id.rb_flash);
        if (item.isColor1CanEdit()) {
            holder.addOnClickListener(R.id.v_color_1);
        }
        holder.addOnClickListener(R.id.v_color_2);
        holder.addOnClickListener(R.id.iv_copy);
        holder.addOnClickListener(R.id.iv_delete);

        initSeekBar(sbOn, sbOff, sbBright, item);
    }

    private void initSeekBar(SeekBar sbOn, SeekBar sbOff, SeekBar sbBright, LedFlashInfo item) {
        sbOn.setProgressDrawable(progressDrawable);
        sbOff.setProgressDrawable(progressDrawable);

        sbOn.setProgress(item.getOnTime() - 10);
        sbOff.setProgress(item.getOffTime() - 10);
        sbBright.setProgress(item.getBright() - 10);

        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBar.getParent().requestDisallowInterceptTouchEvent(true);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.getParent().requestDisallowInterceptTouchEvent(false);
                switch (seekBar.getId()) {
                    case R.id.sb_on:
                        item.setOnTime(seekBar.getProgress() + 10);
                        break;
                    case R.id.sb_off:
                        item.setOffTime(seekBar.getProgress() + 10);
                        break;
                    case R.id.sb_bright:
                        item.setBright(seekBar.getProgress() + 10);
                        break;
                    default:
                        break;
                }
            }
        };

        sbOn.setOnSeekBarChangeListener(seekBarChangeListener);
        sbOff.setOnSeekBarChangeListener(seekBarChangeListener);
        sbBright.setOnSeekBarChangeListener(seekBarChangeListener);
    }

    public void setIsDemo(boolean isDemo) {
        this.isDemo = isDemo;
    }


    /*
     case R.id.rb_gradient:
     info.setType(3);
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
     */
}
