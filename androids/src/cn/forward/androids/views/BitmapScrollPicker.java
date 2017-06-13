package cn.forward.androids.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;

import java.util.List;

import cn.forward.androids.R;
import cn.forward.androids.utils.ColorUtil;

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
    private Rect mRectTemp;
    private int mDrawMode = DRAW_MODE_CENTER;

    // item内容缩放倍数
    private float mMinScale = 1;
    private float mMaxScale = 1;


    public BitmapScrollPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BitmapScrollPicker(Context context, AttributeSet attrs,
                              int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRect1 = new Rect();
        mRect2 = new Rect();
        mSpecifiedSizeRect = new Rect();
        mRectTemp = new Rect();

        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
                    R.styleable.BitmapScrollPicker);
            mDrawMode = typedArray.getInt(
                    R.styleable.BitmapScrollPicker_spv_draw_bitmap_mode, mDrawMode);
            mSpecifiedSizeWidth = typedArray.getDimensionPixelOffset(
                    R.styleable.BitmapScrollPicker_spv_draw_bitmap_width, mSpecifiedSizeWidth);
            mSpecifiedSizeHeight = typedArray.getDimensionPixelOffset(
                    R.styleable.BitmapScrollPicker_spv_draw_bitmap_height, mSpecifiedSizeHeight);
            mMinScale = typedArray.getFloat(R.styleable.BitmapScrollPicker_spv_min_scale, mMinScale);
            mMaxScale = typedArray.getFloat(R.styleable.BitmapScrollPicker_spv_max_scale, mMaxScale);
            typedArray.recycle();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mMeasureWidth = getMeasuredWidth();
        mMeasureHeight = getMeasuredHeight();
        // 当view的的大小确定后，选择器中item的某些位置也可确定。当水平滚动时，item的顶部和底部的坐标y可确定；当垂直滚动时，item的左边和右边的坐标x可确定
        if (mDrawMode == DRAW_MODE_FULL) { // 填充
            if (isHorizontal()) {
                mRect2.top = 0;
                mRect2.bottom = mMeasureHeight;
            } else {
                mRect2.left = 0;
                mRect2.right = mMeasureWidth;
            }
        } else if (mDrawMode == DRAW_MODE_SPECIFIED_SIZE) { // 指定大小
            if (mSpecifiedSizeWidth == -1) {
                mSpecifiedSizeWidth = mMeasureWidth;
                mSpecifiedSizeHeight = mMeasureHeight;
            }
            setDrawModeSpecifiedSize(mSpecifiedSizeWidth, mSpecifiedSizeHeight);
        } else { // 居中
            int size;
            if (isHorizontal()) {
                size = Math.min(mMeasureHeight, getItemWidth());
            } else {
                size = Math.min(mMeasureWidth, getItemHeight());
            }
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

        // 根据不同的绘制模式，计算出item内容的最终绘制位置和大小
        // 当水平滚动时，计算item的左边和右边的坐标x；当垂直滚动时，item的顶部和底部的坐标y

        if (mDrawMode == DRAW_MODE_FULL) { // 填充
            span = 0;
            if (isHorizontal()) {
                mRect2.left = (int) top + span;
                mRect2.right = (int) (top + itemSize - span);
            } else {
                mRect2.top = (int) top + span;
                mRect2.bottom = (int) (top + itemSize - span);
            }
            mRectTemp.set(mRect2);
            scale(mRectTemp, relative, itemSize, moveLength);
            canvas.drawBitmap(bitmap, mRect1, mRectTemp, null);
        } else if (mDrawMode == DRAW_MODE_SPECIFIED_SIZE) { // 指定大小
            if (isHorizontal()) {
                span = (itemSize - mSpecifiedSizeWidth) / 2;

                mSpecifiedSizeRect.left = (int) top + span;
                mSpecifiedSizeRect.right = (int) top + span + mSpecifiedSizeWidth;
            } else {
                span = (itemSize - mSpecifiedSizeHeight) / 2;

                mSpecifiedSizeRect.top = (int) top + span;
                mSpecifiedSizeRect.bottom = (int) top + span + mSpecifiedSizeHeight;
            }
            mRectTemp.set(mSpecifiedSizeRect);
            scale(mRectTemp, relative, itemSize, moveLength);
            canvas.drawBitmap(bitmap, mRect1, mRectTemp, null);
        } else { // 居中
            if (isHorizontal()) {
                float scale = mRect2.height() * 1f / bitmap.getHeight();
                span = (int) ((itemSize - bitmap.getWidth() * scale) / 2);
            } else {
                float scale = mRect2.width() * 1f / bitmap.getWidth();
                span = (int) ((itemSize - bitmap.getHeight() * scale) / 2);
            }
            if (isHorizontal()) {
                mRect2.left = (int) (top + span);
                mRect2.right = (int) (top + itemSize - span);
            } else {

                mRect2.top = (int) (top + span);
                mRect2.bottom = (int) (top + itemSize - span);
            }
            mRectTemp.set(mRect2);
            scale(mRectTemp, relative, itemSize, moveLength);
            canvas.drawBitmap(bitmap, mRect1, mRectTemp, null);
        }
    }

    // 缩放item内容
    private void scale(Rect rect, int relative, int itemSize, float moveLength) {
        if (mMinScale == 1 && mMaxScale == 1) {
            return;
        }

        float spanWidth, spanHeight;

        if (mMinScale == mMaxScale) {
            spanWidth = (rect.width() - mMinScale * rect.width()) / 2;
            spanHeight = (rect.height() - mMinScale * rect.height()) / 2;
            rect.left += spanWidth;
            rect.right -= spanWidth;
            rect.top += spanHeight;
            rect.bottom -= spanHeight;
            return;
        }

        if (relative == -1 || relative == 1) { // 上一个或下一个
            // 处理上一个item且向上滑动　或者　处理下一个item且向下滑动,
            if ((relative == -1 && moveLength < 0)
                    || (relative == 1 && moveLength > 0)) {
                spanWidth = (rect.width() - mMinScale * rect.width()) / 2;
                spanHeight = (rect.height() - mMinScale * rect.height()) / 2;
            } else { // 计算渐变
                float rate = Math.abs(moveLength) / itemSize;
                spanWidth = (rect.width() - (mMinScale + (mMaxScale - mMinScale) * rate) * rect.width()) / 2;
                spanHeight = (rect.height() - (mMinScale + (mMaxScale - mMinScale) * rate) * rect.height()) / 2;
            }
        } else if (relative == 0) { // 中间item
            float rate = (itemSize - Math.abs(moveLength)) / itemSize;
            spanWidth = (rect.width() - (mMinScale + (mMaxScale - mMinScale) * rate) * rect.width()) / 2;
            spanHeight = (rect.height() - (mMinScale + (mMaxScale - mMinScale) * rate) * rect.height()) / 2;
        } else {
            spanWidth = (rect.width() - mMinScale * rect.width()) / 2;
            spanHeight = (rect.height() - mMinScale * rect.height()) / 2;
        }

        rect.left += spanWidth;
        rect.right -= spanWidth;
        rect.top += spanHeight;
        rect.bottom -= spanHeight;

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
        invalidate();
    }

    /**
     * 图片绘制模式 ，默认为居中
     *
     * @return
     */
    public int getDrawMode() {
        return mDrawMode;
    }

    /**
     * item内容缩放倍数
     *
     * @param minScale 沒有被选中时的最小倍数
     * @param maxScale 被选中时的最大倍数
     */
    public void setItemScale(float minScale, float maxScale) {
        mMinScale = minScale;
        mMaxScale = maxScale;
        invalidate();
    }

    public float getMinScale() {
        return mMinScale;
    }

    public float getMaxScale() {
        return mMaxScale;
    }
}