package com.bazooka.bluetoothbox.ui.view.colorpicker;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.utils.ColorUtils;
import com.orhanobut.logger.Logger;

import java.io.InputStream;

public class ColorPickView extends View {
    private Context context;
    private int bigCircle; // 外圈半径
    private int rudeRadius; // 可移动小球的半径
    private int centerColor; // 可移动小球的颜色
    private int selectorResId; // 选择器资源ID
    private Bitmap bitmapBack; // 背景图片
    private Paint mPaint; // 背景画笔
    private Paint mCenterPaint; // 可移动小球画笔
    private Point centerPoint;// 中心位置
    private Point mRockPosition;// 小球中心位置
    private OnColorChangedListener listener; // 小球移动的监听
    private int length; // 小球到中心位置的距离

    private Bitmap mRockBitmap;
    private int selectorWidth;

    BitmapFactory.Options options = new BitmapFactory.Options();

    public ColorPickView(Context context) {
        super(context);
    }

    public ColorPickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    public ColorPickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    public void setOnColorChangedListener(OnColorChangedListener listener) {
        this.listener = listener;
    }

    /**
     * @param attrs
     * @describe 初始化操作
     */
    private void init(AttributeSet attrs) {
        options.inPreferredConfig =  Bitmap.Config.RGB_565;
        // 获取自定义组件的属性
        TypedArray types = context.obtainStyledAttributes(attrs,
                R.styleable.color_picker);
        try {
            //背景圆半径
            bigCircle = (int) types.getDimension(
                    R.styleable.color_picker_circle_radius, 100);
            rudeRadius = (int) types.getDimension(
                    R.styleable.color_picker_center_radius, 10);
            centerColor = types.getColor(R.styleable.color_picker_center_color,
                    Color.WHITE);
            selectorResId = types.getResourceId(R.styleable.color_picker_selector, R.drawable.selector_led_select);

        } finally {
            types.recycle(); // TypeArray用完需要recycle
        }
        // 将背景图片大小设置为属性设置的直径
        bitmapBack = BitmapFactory.decodeResource(getResources(),
                R.drawable.palette, options);


        mRockBitmap = BitmapFactory.decodeResource(getResources(), selectorResId, options);
        mRockBitmap = Bitmap.createScaledBitmap(mRockBitmap, rudeRadius * 2,
                rudeRadius * 2, false);


        selectorWidth = mRockBitmap.getWidth();

        // 中心位置坐标
        centerPoint = new Point(bigCircle, bigCircle);
        mRockPosition = new Point(centerPoint);
    }

    public void setBigCircle(int px) {
        bigCircle = px;
        // 中心位置坐标
        centerPoint = new Point(bigCircle, bigCircle);
        mRockPosition = new Point(centerPoint);
        bitmapBack = Bitmap.createScaledBitmap(bitmapBack, px * 2,
                px * 2, false);

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画背景图片
        if(bitmapBack != null) {
            canvas.drawBitmap(bitmapBack, 0, 0, mPaint);

        }

        canvas.drawBitmap(mRockBitmap, mRockPosition.x - selectorWidth / 2,
                mRockPosition.y - selectorWidth / 2, mPaint);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                length = getLength(event.getX(), event.getY(), centerPoint.x,
                        centerPoint.y);
                if (length > bigCircle) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                length = getLength(event.getX(), event.getY(), centerPoint.x,
                        centerPoint.y);
                if (length <= bigCircle) {
                    mRockPosition.set((int) event.getX(), (int) event.getY());
                } else {
                    mRockPosition = getBorderPoint(centerPoint, new Point(
                            (int) event.getX(), (int) event.getY()), bigCircle);
                }
                if (mRockPosition.x >= bitmapBack.getWidth()) {
                    mRockPosition.x--;
                }
                if (mRockPosition.y >= bitmapBack.getHeight()) {
                    mRockPosition.y--;
                }
                handlerColorChangeListener(bitmapBack.getPixel(Math.abs(mRockPosition.x),
                        Math.abs(mRockPosition.y)));

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                handlerTouchStopListener(bitmapBack.getPixel(Math.abs(mRockPosition.x),
                        Math.abs(mRockPosition.y)));
                break;

            default:
                break;
        }
        invalidate(); // 更新画布
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 视图大小设置为直径
        setMeasuredDimension(bigCircle * 2, bigCircle * 2);
    }

    /**
     * @param eventX
     * @param eventY
     * @param centerX
     * @param centerY
     * @return
     * @describe 计算两点之间的位置
     */
    public int getLength(float eventX, float eventY, float centerX, float centerY) {
        return (int) Math.sqrt(Math.pow(eventX - centerX, 2) + Math.pow(eventY - centerY, 2));
    }

    public int getLength(Point a, Point b) {
        return getLength(a.x, a.y, b.x, b.y);
    }

    public void setRadius(int rudeRadius){
        this.rudeRadius=rudeRadius;
    }

    /**
     * @param centerPoint
     * @param eventPoint
     * @param cutRadius
     * @return
     * @describe 当触摸点超出圆的范围的时候，设置小球边缘位置
     */
    public Point getBorderPoint(Point centerPoint, Point eventPoint, int cutRadius) {
        float radian = getRadian(centerPoint, eventPoint);
        return new Point(centerPoint.x + (int) (cutRadius * Math.cos(radian)), centerPoint.x
                + (int) (cutRadius * Math.sin(radian)));
    }

    /**
     * @param a
     * @param b
     * @return
     * @describe 触摸点与中心点之间直线与水平方向的夹角角度
     */
    public float getRadian(Point a, Point b) {
        float lenA = b.x - a.x;
        float lenB = b.y - a.y;
        float lenC = (float) Math.sqrt(lenA * lenA + lenB * lenB);
        float ang = (float) Math.acos(lenA / lenC);
        ang = ang * (b.y < a.y ? -1 : 1);
        return ang;
    }

    private void handlerColorChangeListener(int color) {
        if (listener != null) {
            int[] rgb = ColorUtils.convertRGB(color);
            int red = rgb[0] <= 3 ? 0 : rgb[0];
            int green = rgb[1] <= 3 ? 0 : rgb[1];
            int blue = rgb[2] <= 3 ? 0 : rgb[2];
            listener.onColorChange(red, green, blue);
        }
    }

    private void handlerTouchStopListener(int color) {
        if (listener != null) {
            int[] rgb = ColorUtils.convertRGB(color);
            int red = rgb[0] <= 3 ? 0 : rgb[0];
            int green = rgb[1] <= 3 ? 0 : rgb[1];
            int blue = rgb[2] <= 3 ? 0 : rgb[2];
            listener.onTouchStop(red, green, blue);
        }
    }

    // 颜色发生变化的回调接口
    public interface OnColorChangedListener {
        void onColorChange(int red, int green, int blue);

        void onTouchStop(int red, int green, int blue);
    }

    public void recycle() {
        mRockBitmap.recycle();
        if(bitmapBack != null) {
            bitmapBack.recycle();
        }
        bitmapBack = null;
        mRockBitmap = null;
    }
}
