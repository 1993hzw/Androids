package cn.forward.androids.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;

import java.util.List;

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

    private int mMeasureWidth;
    private int mMeasureHeight;
    private Rect mRect1, mRect2;
    private int mDrawMode = DRAW_MODE_CENTER;


    public BitmapScrollPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BitmapScrollPicker(Context context, AttributeSet attrs,
                              int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRect1 = new Rect();
        mRect2 = new Rect();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mMeasureWidth = getMeasuredWidth();
        mMeasureHeight = getMeasuredHeight();

        mRect1.left = 0;
        mRect1.top = 0;

        int size = Math.min(mMeasureWidth, getItemHeight());

        if (mDrawMode == DRAW_MODE_FULL) {
            mRect2.left = 0;
            mRect2.right = mMeasureWidth;
        } else {
            mRect2.left = mMeasureWidth / 2 - size / 2;
            mRect2.right = mMeasureWidth / 2 + size / 2;
        }
    }

    @Override
    public void drawItem(Canvas canvas, List<Bitmap> data, int position, int relative, float moveLength, float top) {
        int itemHeight = getItemHeight();
        Bitmap bitmap = data.get(position);

        mRect1.right = bitmap.getWidth();
        mRect1.bottom = bitmap.getHeight();


        mRect2.top = (int) top;
        mRect2.bottom = (int) (top + itemHeight);

        canvas.drawBitmap(bitmap, mRect1, mRect2, null);
    }

    /**
     * 图片绘制模式 ，默认为居中
     *
     * @param mode
     */
    public void setDrawMode(int mode) {
        mDrawMode = mode;
    }

    /**
     * 图片绘制模式 ，默认为居中
     *
     * @return
     */
    public int getDrawMode() {
        if (mDrawMode == DRAW_MODE_FULL) {
            return DRAW_MODE_FULL;
        } else {
            return DRAW_MODE_CENTER;
        }
    }

}
