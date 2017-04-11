package com.yushan.cameraandphotodemo.view;

import android.animation.FloatEvaluator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;

/**
 * 剪切布局
 */
public class ClipImageLayout extends RelativeLayout {
    private ClipZoomImageView mZoomImageView;
    private ClipImageBorderView mClipImageView;

    public ClipImageLayout(Context context) {
        super(context);
        init();
    }

    public ClipImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClipImageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 浮点类型的计算器
//        floatEvaluator = new FloatEvaluator();
    }
    /**
     * 方法功能:获取子控件
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // 简单的异常处理
        if (getChildCount() != 2) {
            throw new IllegalArgumentException("SlideMenu only support 2 children!");
        }

        mClipImageView = (ClipImageBorderView) getChildAt(1);
        mZoomImageView = (ClipZoomImageView) getChildAt(0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        measureChild(mClipImageView, widthMeasureSpec, heightMeasureSpec);
        measureChild(mZoomImageView, widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = 0;
        int top = 0;

        mClipImageView.layout(left,top,mClipImageView.getMeasuredWidth(), mClipImageView.getMeasuredHeight());
        mZoomImageView.layout(left,top,mZoomImageView.getMeasuredWidth(), mZoomImageView.getMeasuredHeight());
    }


    public void setImageDrawable(Drawable drawable) {
        mZoomImageView.setImageDrawable(drawable);
    }

    public void setImageBitmap(Bitmap bitmap) {
        mZoomImageView.setImageBitmap(bitmap);
    }

    /**
     * 对外公布设置边距的方法 单位为dp
     *
     * @param mHorizontalPadding
     */
    public void setHorizontalPadding(int mHorizontalPadding) {
        mClipImageView.setHorizontalPadding(mHorizontalPadding);
        mZoomImageView.setHorizontalPadding(mHorizontalPadding);
    }

    /**
     * 裁切图片
     *
     * @return
     */
    public Bitmap clip() {
        return mZoomImageView.clip();
    }
}
