package cn.forward.androids.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import cn.forward.androids.R;

/**
 * 点击时，显示遮罩 (前提设置了setClickable(true))
 *
 * @author huangziwei
 * @date 2015.12.29
 */
public class MaskImageView extends ImageView {

    private boolean mIsIgnoreAlpha = true; // 是否忽略图片的透明度，默认为true,透明部分不显示遮罩
    private boolean mIsShowMaskOnClick = true; // 点击时是否显示遮罩
    private boolean mIsPressed; // 是否正在点击
    private int mShadeColor = 0x00ffffff; // 遮罩颜色（argb）


    // 默认的颜色滤镜
    ColorFilter mDefaultColorFilter = new ColorMatrixColorFilter(new ColorMatrix());
    ColorMatrix mColorMatrix = new ColorMatrix(); // 颜色矩阵
    ColorMatrixColorFilter mColorFilter;


    public MaskImageView(Context context) {
        this(context, null);
    }

    public MaskImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaskImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
        setClickable(true);
    }

    private void init(AttributeSet attrs) {

        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.MaskImageView);

        mIsIgnoreAlpha = a.getBoolean(R.styleable.MaskImageView_is_ignore_alpha, mIsIgnoreAlpha);
        mIsShowMaskOnClick = a.getBoolean(R.styleable.MaskImageView_is_show_mask_on_click, mIsShowMaskOnClick);
        mShadeColor = a.getColor(R.styleable.MaskImageView_mask_color, mShadeColor);

        setColorMatrix(new float[]{
                0.5f, 0, 0, 0, Color.red(mShadeColor) / 2,
                0, 0.5f, 0, 0, Color.green(mShadeColor) / 2,
                0, 0, 0.5f, 0, Color.blue(mShadeColor) / 2,
                0, 0, 0, 1, 0,
        });

        a.recycle();
    }

    private void setColorMatrix(float[] matrix) {
        mColorMatrix.set(matrix);
        mColorFilter = new ColorMatrixColorFilter(mColorMatrix);
    }

    private void setDrawableColorFilter(ColorFilter colorFilter) {
        if (getDrawable() != null) {
            getDrawable().setColorFilter(colorFilter);
        }
    }


    /* 忽略透明度，添加遮罩原理

      创建新的滤镜
      ColorMatrix colorMatrix = new ColorMatrix(new float[]{
         a,b,c,d,e,
         f,g,h,i,j,
         k,l,m,n,o,
         p,q,r,s,t});

      已知一个颜色值ARGB，则经过下面的矩阵运算可得出新的颜色值
      int red   = a*R + b*R + c*R + d*R + e;
      int green = f*G + g*G + h*G + i*G + j;
      int blue  = k*B + l*B + m*B + n*B + o;
      int alpha = p*A + q*A + r*A + s*A + t;

      设置图片滤镜
      getDrawable().setColorFilter(new ColorMatrixColorFilter(colorMatrix));

      绘图
      mDrawable.draw(canvas)

    */
    @Override
    protected void onDraw(Canvas canvas) {

        if (mIsIgnoreAlpha) { // 忽略透明度
            if (mIsShowMaskOnClick && mIsPressed && isClickable()) {
                // 绘制遮罩层
                setDrawableColorFilter(mColorFilter);
            } else {
                setDrawableColorFilter(mDefaultColorFilter);
            }
            super.onDraw(canvas);

        } else { // 不忽略透明度，则直接在view上画遮罩
            setDrawableColorFilter(mDefaultColorFilter);
            super.onDraw(canvas);

            if (mIsShowMaskOnClick && mIsPressed && isClickable()) {
                // 绘制遮罩层
                canvas.drawColor(mShadeColor);
            }
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!mIsShowMaskOnClick) {
            return super.onTouchEvent(event);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsPressed = true;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_HOVER_EXIT:
            case MotionEvent.ACTION_CANCEL:
                // 当手指离开imageview区域时，取消遮罩
                mIsPressed = false;
                invalidate();
                break;
            default:
                break;
        }
        // 不返回true，即不拦截触摸事件
        return super.onTouchEvent(event);
    }

}