package com.bazooka.bluetoothbox.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.bazooka.bluetoothbox.R;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/12/20
 *         作用：闪屏页文字动画
 */

public class AnimateText extends AppCompatTextView {

    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean annimaStart = false; // 开始时是否展示动画
    private int time = 20;// 动画时长
    private OnAnimationEndListener listener;// 动画结束时的监听
    private String text;// 文本

    public AnimateText(Context context) {
        this(context, null);
    }

    public AnimateText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimateText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AnimateText);
            annimaStart = a.getBoolean(R.styleable.AnimateText_annimaStart, false);
            time = a.getInteger(R.styleable.AnimateText_time, 20);
            a.recycle();
        }
        text = getText().toString();
        if (annimaStart) {
            startAnimate();
        }
    }

    public void startAnimate() {
        this.startAnimate(null);
    }

    public void startAnimate(final OnAnimationEndListener listener) {
        this.listener = listener;
        new Thread(this::showNormalAnimate).start();
    }

    private void showNormalAnimate() {
        char[] cs = text.toCharArray();
        final StringBuilder buffer = new StringBuilder();
        for (char c : cs) {
            buffer.append(c);
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handler.post(() -> AnimateText.this.setText(buffer.toString(), BufferType.NORMAL));
        }
        handler.post(() -> {
            if (listener != null) {
                listener.onEnd();
            }
        });
    }


    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isAnnimaStart() {
        return annimaStart;
    }

    public void setAnnimaStart(boolean annimaStart) {
        this.annimaStart = annimaStart;
    }


    public void setText(String text) {
        this.text = text;
//        setText(text, BufferType.NORMAL);
    }

    public OnAnimationEndListener getListener() {
        return listener;
    }

    public void setListener(OnAnimationEndListener listener) {
        this.listener = listener;
    }

    public interface OnAnimationEndListener {
        void onEnd();
    }
}