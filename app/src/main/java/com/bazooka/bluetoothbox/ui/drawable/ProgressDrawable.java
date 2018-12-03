package com.bazooka.bluetoothbox.ui.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/10/19
 * 作用：自定义闪法中 ON 和 OFF 的SeekBar 的背景图
 */

public class ProgressDrawable extends Drawable {
    private Paint mPaint;

    public ProgressDrawable() {
        mPaint = new Paint();
        mPaint.setStrokeWidth(1);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect r = getBounds();

        int width = r.right - r.left;
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLACK);
        for (int i = 0; i < width; i += 2) {
            //画竖线
            canvas.drawLine(r.left + i, r.top, r.left + i, r.bottom, mPaint);
        }
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(0xfff48300);
        Rect yellowRect = new Rect();
        yellowRect.left = r.left;
        yellowRect.right = r.right;
        yellowRect.top = r.top + (r.bottom - r.top) / 2 / 2;
        yellowRect.bottom = r.bottom - (r.bottom - r.top) / 2 / 2;

        canvas.drawRect(yellowRect, mPaint);
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        mPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
