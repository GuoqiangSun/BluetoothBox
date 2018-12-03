package com.bazooka.bluetoothbox.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.bazooka.bluetoothbox.R;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/12/15
 *         作用：上拉抽屉布局效果
 *         参考文章：http://www.jianshu.com/p/0e8ed99b4fb9
 *
 *         注意：ContentView 和 BottomView 可不写在属性中，可直接作为子 View，但顺序不能变，
 *              先写 ContentView 再写 BottomView
 */

public class PullUpDragLayout extends ViewGroup {

    /**
     * 拖拽帮助类
     */
    private ViewDragHelper mViewDragHelper;
    /**
     * 底部View
     */
    private View mBottomView;
    /**
     * 内容View
     */
    private View mContentView;
    /**
     * 底部边界突出高度
     */
    private int mBottomBorder = 20;

    private Point mAutoBackBottomPos = new Point();
    private Point mAutoBackTopPos = new Point();
    private int mBoundTopY;
    private boolean isOpen;

    private OnStateListener mOnStateListener;
    private OnScrollChageListener mScrollChageListener;


    public PullUpDragLayout(Context context) {
        this(context, null, 0);
    }

    public PullUpDragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public PullUpDragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(21)
    public PullUpDragLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater mLayoutInflater = LayoutInflater.from(context);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PullUpDragLayout);

        if (typedArray.hasValue(R.styleable.PullUpDragLayout_PullUpDrag_BottomView)) {
            mBottomView = mLayoutInflater.inflate(
                    typedArray.getResourceId(R.styleable.PullUpDragLayout_PullUpDrag_BottomView, 0),
                    this, true);
        }
        if (typedArray.hasValue(R.styleable.PullUpDragLayout_PullUpDrag_ContentView)) {
            mContentView = mLayoutInflater.inflate(
                    typedArray.getResourceId(R.styleable.PullUpDragLayout_PullUpDrag_ContentView, 0),
                    this, true);
        }

        mBottomBorder = typedArray.getDimensionPixelSize(
                R.styleable.PullUpDragLayout_PullUpDrag_BottomBorderHeight, 20);

        typedArray.recycle();

        mViewDragHelper = ViewDragHelper.create(this, 1.0f, mDragCallback);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mContentView = getChildAt(0);
        mBottomView = getChildAt(1);

        measureChild(mBottomView, widthMeasureSpec, heightMeasureSpec);
        int bottomHeight = mBottomView.getMeasuredHeight();
        measureChild(mContentView, widthMeasureSpec, heightMeasureSpec);
        int contentHeight = mContentView.getMeasuredHeight();

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                bottomHeight + contentHeight + getPaddingTop() + getPaddingBottom());

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mContentView = getChildAt(0);
        mBottomView = getChildAt(1);

        mContentView.layout(getPaddingLeft(), getPaddingTop(),
                getWidth() - getPaddingRight(), mContentView.getMeasuredHeight());
        if(isOpen) {
            mBottomView.layout(getPaddingLeft(), mContentView.getHeight() - mBottomView.getHeight(),
                    getWidth() - getPaddingRight(), mContentView.getHeight());
        } else {
            mBottomView.layout(getPaddingLeft(), mContentView.getHeight() - mBottomBorder,
                    getWidth() - getPaddingRight(), getMeasuredHeight() - mBottomBorder);
        }

        mAutoBackBottomPos.x = mBottomView.getLeft();
        mAutoBackBottomPos.y = mContentView.getHeight() - mBottomBorder;

        mAutoBackTopPos.x = mBottomView.getLeft();
        mAutoBackTopPos.y = mContentView.getHeight() - mBottomView.getHeight();
        mBoundTopY = mContentView.getHeight() - mBottomView.getHeight() / 3;
    }

    ViewDragHelper.Callback mDragCallback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return mBottomView == child;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return super.clampViewPositionHorizontal(child, left, dx);
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            int topBound = mContentView.getHeight() - mBottomView.getHeight();
            int bottomBound = mContentView.getHeight() - mBottomBorder;
            return Math.min(bottomBound, Math.max(top, topBound));
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return getMeasuredHeight() - child.getMeasuredHeight();
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == mBottomView) {
                float startPosition = mContentView.getHeight() - mBottomView.getHeight();
                float endPosition = mContentView.getHeight() - mBottomBorder;
                float totalLength = endPosition - startPosition;
                float rate = 1 - ((top - startPosition) / totalLength);

                if (mScrollChageListener != null) {
                    mScrollChageListener.onScrollChange(rate);
                }

            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (releasedChild == mBottomView) {
                if (releasedChild.getY() < mBoundTopY || yvel <= -1000) {
                    mViewDragHelper.settleCapturedViewAt(mAutoBackTopPos.x, mAutoBackTopPos.y);
                    isOpen = true;
                    mBottomView.setClickable(true);
                    if (mOnStateListener != null) {
                        mOnStateListener.open();
                    }
                } else if (releasedChild.getY() >= mBoundTopY || yvel >= 1000) {
                    mViewDragHelper.settleCapturedViewAt(mAutoBackBottomPos.x, mAutoBackBottomPos.y);
                    isOpen = false;
                    mBottomView.setClickable(false);
                    if (mOnStateListener != null) {
                        mOnStateListener.close();
                    }
                }
                invalidate();
            }
        }
    };



    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        if(isOpen) {
            mBottomView.addFocusables(views, direction, focusableMode);
        } else {
            mContentView.addFocusables(views, direction, focusableMode);
        }
    }

    public boolean isOpen() {
        return isOpen;
    }

    /**
     * 切换底部View
     */
    public void toggleBottomView() {
        if (isOpen) {
            mViewDragHelper.smoothSlideViewTo(mBottomView, mAutoBackBottomPos.x, mAutoBackBottomPos.y);
            if (mOnStateListener != null) {
                mOnStateListener.close();
            }
        } else {
            mViewDragHelper.smoothSlideViewTo(mBottomView, mAutoBackTopPos.x, mAutoBackTopPos.y);
            if (mOnStateListener != null) {
                mOnStateListener.open();
            }
        }
        invalidate();
        isOpen = !isOpen;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean interceptForDrag = mViewDragHelper.shouldInterceptTouchEvent(ev);

        boolean interceptForTap = false;

        if(isOpen) {
            final View child = mViewDragHelper.findTopChildUnder((int) ev.getX(), (int) ev.getY());
            if(child == mContentView) {
                interceptForTap = true;
            }
        }

        return interceptForDrag || interceptForTap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    public void setOnStateListener(OnStateListener onStateListener) {
        mOnStateListener = onStateListener;
    }

    public void setOnScrollChageListener(OnScrollChageListener scrollChageListener) {
        mScrollChageListener = scrollChageListener;
    }

    public interface OnStateListener {
        void open();

        void close();
    }

    public interface OnScrollChageListener {
        void onScrollChange(float rate);
    }
}
