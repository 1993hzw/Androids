package cn.forward.androids.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.forward.androids.R;
import cn.forward.androids.utils.ColorUtil;

/**
 * 字符串滚动选择器
 * Created by huangziwei on 16-12-6.
 */
public class StringScrollPicker extends ScrollPickerView<String> {


    private int mMeasureWidth;
    private int mMeasureHeight;

    private Paint mPaint; //
    private int mMinTextSize = 24; // 最小的字体
    private int mMaxTextSize = 32; // 最大的字体
    // 字体渐变颜色
    private int mStartColor = Color.BLACK; // 中间选中ｉｔｅｍ的颜色
    private int mEndColor = Color.GRAY; // 上下两边的颜色


    public StringScrollPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StringScrollPicker(Context context, AttributeSet attrs,
                              int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLACK);
        init(attrs);

        setData(new ArrayList<String>(Arrays.asList(new String[]{
                "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve"
        })));

    }


    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
                    R.styleable.StringScrollPicker);
            mMinTextSize = typedArray.getDimensionPixelSize(
                    R.styleable.StringScrollPicker_spv_min_text_size, mMinTextSize);
            mMaxTextSize = typedArray.getDimensionPixelSize(
                    R.styleable.StringScrollPicker_spv_max_text_size, mMaxTextSize);
            mStartColor = typedArray.getColor(
                    R.styleable.StringScrollPicker_spv_start_color, mStartColor);
            mEndColor = typedArray.getColor(
                    R.styleable.StringScrollPicker_spv_end_color, mEndColor);
            typedArray.recycle();
        }
    }

    /**
     * @param startColor 正中间的颜色
     * @param endColor   上下两边的颜色
     */
    public void setColor(int startColor, int endColor) {
        mStartColor = startColor;
        mEndColor = endColor;
        invalidate();
    }

    /**
     * item文字大小
     *
     * @param minText 沒有被选中时的最小文字
     * @param maxText 被选中时的最大文字
     */
    public void setTextSize(int minText, int maxText) {
        mMinTextSize = minText;
        mMaxTextSize = maxText;
        invalidate();
    }

    public int getStartColor() {
        return mStartColor;
    }

    public int getEndColor() {
        return mEndColor;
    }

    public int getMinTextSize() {
        return mMinTextSize;
    }

    public int getMaxTextSize() {
        return mMaxTextSize;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mMeasureWidth = getMeasuredWidth();
        mMeasureHeight = getMeasuredHeight();
    }

    @Override
    public void drawItem(Canvas canvas, List<String> data, int position, int relative, float moveLength, float top) {
        String text = data.get(position);
        int itemSize = getItemSize();

        // 设置文字大小
        if (relative == -1) { // 上一个
            if (moveLength < 0) { // 向上滑动
                mPaint.setTextSize(mMinTextSize);
            } else { // 向下滑动
                mPaint.setTextSize(mMinTextSize + (mMaxTextSize - mMinTextSize)
                        * moveLength / itemSize);
            }
        } else if (relative == 0) { // 中间item,当前选中
            mPaint.setTextSize(mMinTextSize + (mMaxTextSize - mMinTextSize)
                    * (itemSize - Math.abs(moveLength)) / itemSize);
        } else if (relative == 1) { // 下一个
            if (moveLength > 0) { // 向下滑动
                mPaint.setTextSize(mMinTextSize);
            } else { // 向上滑动
                mPaint.setTextSize(mMinTextSize + (mMaxTextSize - mMinTextSize)
                        * -moveLength / itemSize);
            }
        } else { // 其他
            mPaint.setTextSize(mMinTextSize);
        }
        float x = 0;
        float y = 0;
        if (isHorizontal()) { // 水平滚动
            Paint.FontMetricsInt fmi = mPaint.getFontMetricsInt();
            x = top + (itemSize - mPaint.measureText(text)) / 2;
            y = mMeasureHeight / 2 - fmi.descent + (fmi.bottom - fmi.top) / 2;
        } else { // 垂直滚动
            x = (mMeasureWidth - mPaint.measureText(text)) / 2;
            Paint.FontMetricsInt fmi = mPaint.getFontMetricsInt();
            // 绘制文字时，文字的baseline是对齐ｙ坐标的，下面换算使其垂直居中。fmi.top值是相对baseline的，为负值
            y = top + itemSize / 2
                    - fmi.descent + (fmi.bottom - fmi.top) / 2;
        }
        // 计算渐变颜色
        computeColor(relative, itemSize, moveLength);
        canvas.drawText(text, x, y, mPaint);
    }

    /**
     * 计算字体颜色，渐变
     *
     * @param relative 　相对中间item的位置
     */
    private void computeColor(int relative, int itemSize, float moveLength) {

        int color = mEndColor; // 　其他默认为ｍEndColor

        if (relative == -1 || relative == 1) { // 上一个或下一个
            // 处理上一个item且向上滑动　或者　处理下一个item且向下滑动　，颜色为mEndColor
            if ((relative == -1 && moveLength < 0)
                    || (relative == 1 && moveLength > 0)) {
                color = mEndColor;
            } else { // 计算渐变的颜色
                float rate = (itemSize - Math.abs(moveLength))
                        / itemSize;
                color = ColorUtil.computeGradientColor(mStartColor, mEndColor, rate);
            }
        } else if (relative == 0) { // 中间item
            float rate = Math.abs(moveLength) / itemSize;
            color = ColorUtil.computeGradientColor(mStartColor, mEndColor, rate);
        }

        mPaint.setColor(color);
    }
}
