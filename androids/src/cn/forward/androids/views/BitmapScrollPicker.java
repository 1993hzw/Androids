package cn.forward.androids.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;

import java.util.List;

import cn.forward.androids.R;

/**
 * 图片滚动选择器
 * Created by huangziwei on 16-12-7.
 */
public class BitmapScrollPicker extends ScrollPickerView<Bitmap> {

    /**
     * 图片绘制模式：填充
     */
    public final static int DRAW_MODE_FULL = 1; //
    /**
     * 图片绘制模式：居中
     */
    public final static int DRAW_MODE_CENTER = 2; //
    /**
     * 图片绘制模式：指定大小
     */
    public final static int DRAW_MODE_SPECIFIED_SIZE = 3; //

    private int mMeasureWidth;
    private int mMeasureHeight;
    private Rect mRect1, mRect2, mSpecifiedSizeRect;
    private int mDrawMode = DRAW_MODE_CENTER;


    public BitmapScrollPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BitmapScrollPicker(Context context, AttributeSet attrs,
                              int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRect1 = new Rect();
        mRect2 = new Rect();
        mSpecifiedSizeRect = new Rect();

        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
                    R.styleable.ScrollPickerView);
            mDrawMode = typedArray.getInt(
                    R.styleable.ScrollPickerView_spv_draw_bitmap_mode, mDrawMode);
            mSpecifiedSizeWidth = typedArray.getDimensionPixelOffset(
                    R.styleable.ScrollPickerView_spv_draw_bitmap_width, mSpecifiedSizeWidth);
            mSpecifiedSizeHeight = typedArray.getDimensionPixelOffset(
                    R.styleable.ScrollPickerView_spv_draw_bitmap_height, mSpecifiedSizeHeight);
            typedArray.recycle();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mMeasureWidth = getMeasuredWidth();
        mMeasureHeight = getMeasuredHeight();

        mRect1.left = 0;
        mRect1.top = 0;

        int size;
        if (isHorizontal()) {
            size = Math.min(mMeasureHeight, getItemWidth());
        } else {
            size = Math.min(mMeasureWidth, getItemHeight());
        }

        if (mDrawMode == DRAW_MODE_FULL) {
            if (isHorizontal()) {
                mRect2.top = 0;
                mRect2.bottom = mMeasureHeight;
            } else {
                mRect2.left = 0;
                mRect2.right = mMeasureWidth;
            }
        } else if (mDrawMode == DRAW_MODE_SPECIFIED_SIZE) {
            if (mSpecifiedSizeWidth == -1) {
                mSpecifiedSizeWidth = mMeasureWidth;
                mSpecifiedSizeHeight = mMeasureHeight;
            }
            setDrawModeSpecifiedSize(mSpecifiedSizeWidth, mSpecifiedSizeHeight);
        } else { // 居中
            if (isHorizontal()) {
                mRect2.top = mMeasureHeight / 2 - size / 2;
                mRect2.bottom = mMeasureHeight / 2 + size / 2;
            } else {
                mRect2.left = mMeasureWidth / 2 - size / 2;
                mRect2.right = mMeasureWidth / 2 + size / 2;
            }
        }
    }

    @Override
    public void drawItem(Canvas canvas, List<Bitmap> data, int position, int relative, float moveLength, float top) {
        int itemSize = getItemSize();
        Bitmap bitmap = data.get(position);

        mRect1.right = bitmap.getWidth();
        mRect1.bottom = bitmap.getHeight();

        int span = 0;

        if (mDrawMode == DRAW_MODE_FULL) {
            span = 0;
            if (isHorizontal()) {
                mRect2.left = (int) top + span;
                mRect2.right = (int) (top + itemSize - span);
            } else {
                mRect2.top = (int) top + span;
                mRect2.bottom = (int) (top + itemSize - span);
            }
            canvas.drawBitmap(bitmap, mRect1, mRect2, null);
        } else if (mDrawMode == DRAW_MODE_SPECIFIED_SIZE) {
            if (isHorizontal()) {
                span = (itemSize - mSpecifiedSizeWidth) / 2;
                if (span < 0) { // 图片不能超过item
                    span = 0;
                }

                mSpecifiedSizeRect.left = (int) top + span;
                mSpecifiedSizeRect.right = (int) top + span + mSpecifiedSizeWidth;
            } else {
                span = (itemSize - mSpecifiedSizeHeight) / 2;
                if (span < 0) { // 图片不能超过item
                    span = 0;
                }

                mSpecifiedSizeRect.top = (int) top + span;
                mSpecifiedSizeRect.bottom = (int) top + span + mSpecifiedSizeHeight;
            }
            canvas.drawBitmap(bitmap, mRect1, mSpecifiedSizeRect, null);
        } else {
            if (isHorizontal()) {
                float scale = mRect2.height() * 1f / bitmap.getHeight();
                span = (int) ((itemSize - bitmap.getWidth() * scale) / 2);
            } else {
                float scale = mRect2.width() * 1f / bitmap.getWidth();
                span = (int) ((itemSize - bitmap.getHeight() * scale) / 2);
            }
            if (span < 0) { // 图片不能超过item
                span = 0;
            }
            if (isHorizontal()) {
                mRect2.left = (int) (top + span);
                mRect2.right = (int) (top + itemSize - span);
            } else {

                mRect2.top = (int) (top + span);
                mRect2.bottom = (int) (top + itemSize - span);
            }
            canvas.drawBitmap(bitmap, mRect1, mRect2, null);
        }


    }

    /**
     * 图片绘制模式 ，默认为居中
     *
     * @param mode
     */
    public void setDrawMode(int mode) {
        int size = 0;
        if (isHorizontal()) {
            size = Math.min(mMeasureHeight, getItemWidth());
        } else {
            size = Math.min(mMeasureWidth, getItemHeight());
        }
        mDrawMode = mode;
        if (mDrawMode == DRAW_MODE_FULL) {
            if (isHorizontal()) {
                mRect2.top = 0;
                mRect2.bottom = mMeasureHeight;
            } else {
                mRect2.left = 0;
                mRect2.right = mMeasureWidth;
            }
        } else if (mDrawMode == DRAW_MODE_SPECIFIED_SIZE) {

        } else {
            if (isHorizontal()) {
                mRect2.top = mMeasureHeight / 2 - size / 2;
                mRect2.bottom = mMeasureHeight / 2 + size / 2;
            } else {
                mRect2.left = mMeasureWidth / 2 - size / 2;
                mRect2.right = mMeasureWidth / 2 + size / 2;
            }
        }
        invalidate();
    }

    private int mSpecifiedSizeWidth = -1;
    private int mSpecifiedSizeHeight = -1;

    public void setDrawModeSpecifiedSize(int width, int height) {
        if (isHorizontal()) {
            mSpecifiedSizeRect.top = (mMeasureHeight - height) / 2;
            mSpecifiedSizeRect.bottom = (mMeasureHeight - height) / 2 + height;
        } else {
            mSpecifiedSizeRect.left = (mMeasureWidth - width) / 2;
            mSpecifiedSizeRect.right = (mMeasureWidth - width) / 2 + width;
        }
        mSpecifiedSizeWidth = width;
        mSpecifiedSizeHeight = height;


    }

    /**
     * 图片绘制模式 ，默认为居中
     *
     * @return
     */
    public int getDrawMode() {
        return mDrawMode;
    }

}