package cn.forward.androids.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import cn.forward.androids.R;


/**
 * 可以设置宽度和高度的比例，
 *
 * @author huangziwei
 * @date 2015.12.29
 */
public class RatioImageView extends ImageView {

    /* 优先级从大到小：
     mIsWidthFitDrawableSizeRatio mIsHeightFitDrawableSizeRatio
     mWidthRatio mHeightRatio
     即如果设置了mIsWidthFitDrawableSizeRatio为true，则优先级较低的三个值不生效 */

    private float mDrawableSizeRatio = -1f; // src图片(前景图)的宽高比例
    // 根据前景图宽高比例测量View,防止图片缩放变形
    private boolean mIsWidthFitDrawableSizeRatio; // 宽度是否根据src图片(前景图)的比例来测量（高度已知）
    private boolean mIsHeightFitDrawableSizeRatio; // 高度是否根据src图片(前景图)的比例来测量（宽度已知）
    private int mMaxWidthWhenWidthFixDrawable = -1; // 当mIsWidthFitDrawableSizeRatio生效时，最大宽度
    private int mMaxHeightWhenHeightFixDrawable = -1; // 当mIsHeightFitDrawableSizeRatio生效时，最大高度

    // 宽高比例
    private float mWidthRatio = -1; // 宽度 = 高度*mWidthRatio
    private float mHeightRatio = -1; // 高度 = 宽度*mHeightRatio

    private int mDesiredWidth = -1; // 宽度和高度,避免layout_width/layout_height会在超过屏幕尺寸时特殊处理的情况
    private int mDesiredHeight = -1;

    public RatioImageView(Context context) {
        this(context, null);
    }

    public RatioImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr); // 虽然此处会调用setImageDrawable，但此时成员变量还未被正确初始化
        init(attrs);
        // 一定要有此代码
        if (getDrawable() != null) {
            mDrawableSizeRatio = 1f * getDrawable().getIntrinsicWidth()
                    / getDrawable().getIntrinsicHeight();
        }
    }

    /**
     * 初始化变量
     */
    private void init(AttributeSet attrs) {

        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.RatioImageView);
        mIsWidthFitDrawableSizeRatio = a.getBoolean(R.styleable.RatioImageView_riv_is_width_fix_drawable_size_ratio,
                mIsWidthFitDrawableSizeRatio);
        mIsHeightFitDrawableSizeRatio = a.getBoolean(R.styleable.RatioImageView_riv_is_height_fix_drawable_size_ratio,
                mIsHeightFitDrawableSizeRatio);
        mMaxWidthWhenWidthFixDrawable = a.getDimensionPixelOffset(R.styleable.RatioImageView_riv_max_width_when_width_fix_drawable,
                mMaxWidthWhenWidthFixDrawable);
        mMaxHeightWhenHeightFixDrawable = a.getDimensionPixelOffset(R.styleable.RatioImageView_riv_max_height_when_height_fix_drawable,
                mMaxHeightWhenHeightFixDrawable);
        mHeightRatio = a.getFloat(
                R.styleable.RatioImageView_riv_height_to_width_ratio, mHeightRatio);
        mWidthRatio = a.getFloat(
                R.styleable.RatioImageView_riv_width_to_height_ratio, mWidthRatio);
        mDesiredWidth = a.getDimensionPixelOffset(R.styleable.RatioImageView_riv_width, mDesiredWidth);
        mDesiredHeight = a.getDimensionPixelOffset(R.styleable.RatioImageView_riv_height, mDesiredHeight);

        a.recycle();
    }

    private void onSetDrawable() {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            // 发生变化，重新调整布局
            if (mIsWidthFitDrawableSizeRatio || mIsHeightFitDrawableSizeRatio) {
                float old = mDrawableSizeRatio;
                mDrawableSizeRatio = 1f * drawable.getIntrinsicWidth()
                        / drawable.getIntrinsicHeight();
                if (old != mDrawableSizeRatio && mDrawableSizeRatio > 0) {
                    requestLayout();
                }
            }
        }
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        onSetDrawable();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        onSetDrawable();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 优先级从大到小：
        // mIsWidthFitDrawableSizeRatio mIsHeightFitDrawableSizeRatio
        // mWidthRatio mHeightRatio
        if (mDrawableSizeRatio > 0) {
            // 根据前景图宽高比例来测量view的大小
            if (mIsWidthFitDrawableSizeRatio) {
                mWidthRatio = mDrawableSizeRatio;
            } else if (mIsHeightFitDrawableSizeRatio) {
                mHeightRatio = 1 / mDrawableSizeRatio;
            }
        }

        if (mHeightRatio > 0 && mWidthRatio > 0) {
            throw new RuntimeException("高度和宽度不能同时设置百分比！！");
        }

        if (mWidthRatio > 0) { // 高度已知，根据比例，设置宽度
            int height = 0;
            if (mDesiredHeight > 0) {
                height = mDesiredHeight;
            } else {
                height = MeasureSpec.getSize(heightMeasureSpec);
            }
            int width = (int) (height * mWidthRatio);
            if (mIsWidthFitDrawableSizeRatio && mMaxWidthWhenWidthFixDrawable > 0
                    && width > mMaxWidthWhenWidthFixDrawable) { // 限制最大宽度
                width = mMaxWidthWhenWidthFixDrawable;
                height = (int) (width / mWidthRatio);
            }
            super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        } else if (mHeightRatio > 0) { // 宽度已知，根据比例，设置高度
            int width = 0;
            if (mDesiredWidth > 0) {
                width = mDesiredWidth;
            } else {
                width = MeasureSpec.getSize(widthMeasureSpec);
            }
            int height = (int) (width * mHeightRatio);
            if (mIsHeightFitDrawableSizeRatio && mMaxHeightWhenHeightFixDrawable > 0
                    && height > mMaxHeightWhenHeightFixDrawable) { // 限制最大高度
                height = mMaxHeightWhenHeightFixDrawable;
                width = (int) (height / mHeightRatio);
            }
            super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        } else if (mDesiredHeight > 0 && mDesiredWidth > 0) { // 当没有设置其他属性时，width和height必须同时设置才生效
            int width = mDesiredWidth;
            int height = mDesiredHeight;
            super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        } else { // 系统默认测量
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * 同时设置这两个值,如果两个值都为true，则isWidthFitDrawableSizeRatio优先
     *
     * @param isWidthFitDrawableSizeRatio
     * @param isHeightFitDrawableSizeRatio
     */
    public void setIsFitDrawableSizeRatio(
            boolean isWidthFitDrawableSizeRatio, boolean isHeightFitDrawableSizeRatio) {
        mWidthRatio = mHeightRatio = -1;
        boolean oldIsWidth = mIsWidthFitDrawableSizeRatio;
        boolean oldIsHeight = mIsHeightFitDrawableSizeRatio;
        this.mIsWidthFitDrawableSizeRatio = isWidthFitDrawableSizeRatio;
        this.mIsHeightFitDrawableSizeRatio = isHeightFitDrawableSizeRatio;
        Drawable drawable = getDrawable();
        if (drawable != null) {
            mDrawableSizeRatio = 1f * drawable.getIntrinsicWidth()
                    / drawable.getIntrinsicHeight();
        } else {
            mDrawableSizeRatio = -1;
        }
        if (oldIsWidth != mIsWidthFitDrawableSizeRatio
                || oldIsHeight != mIsHeightFitDrawableSizeRatio) {
            requestLayout();
        }
    }

    /*
     * 设置宽度的比例，高度比例失效mHeightRatio = -1
     */
    public void setWidthRatio(float mWidthRatio) {
        mIsWidthFitDrawableSizeRatio = mIsHeightFitDrawableSizeRatio = false;
        float oldWidthRatio = mWidthRatio;
        float oldHeightRatio = mHeightRatio;
        this.mHeightRatio = -1;
        this.mWidthRatio = mWidthRatio;
        if (oldWidthRatio != mWidthRatio || oldHeightRatio != mHeightRatio) {
            requestLayout();
        }
    }

    /*
     * 设置高度的比例，宽度比例失效mWidthRatio = -1
     */
    public void setHeightRatio(float mHeightRatio) {
        mIsWidthFitDrawableSizeRatio = mIsHeightFitDrawableSizeRatio = false;
        float oldWidthRatio = mWidthRatio;
        float oldHeightRatio = mHeightRatio;
        this.mWidthRatio = -1;
        this.mHeightRatio = mHeightRatio;
        if (oldWidthRatio != mWidthRatio || oldHeightRatio != mHeightRatio) {
            requestLayout();
        }
    }

    public void setWidthAndHeight(int width, int height) {
        int oldW = mDesiredWidth;
        int oldH = mDesiredHeight;
        mDesiredWidth = width;
        mDesiredHeight = height;
        if (oldW != mDesiredWidth || oldH != mDesiredHeight) {
            requestLayout();
        }
    }


    public boolean isIsWidthFitDrawableSizeRatio() {
        return mIsWidthFitDrawableSizeRatio;
    }

    public boolean isIsHeightFitDrawableSizeRatio() {
        return mIsHeightFitDrawableSizeRatio;
    }

    public float getWidthRatio() {
        return mWidthRatio;
    }

    public float getHeightRatio() {
        return mHeightRatio;
    }

    public float getDrawableSizeRatio() {
        return mDrawableSizeRatio;
    }

}
