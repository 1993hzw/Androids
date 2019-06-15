package cn.forward.androids.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import cn.forward.androids.R;

/**
 * 直接在xml布局文件设置View的内容padding, 支持靠边对齐
 * 用于解决点击区域大于内容区域的问题
 */
public class PaddingViewAttrs {

    @SuppressLint("ResourceType")
    public static void obtainsAttrs(Context context, View view, AttributeSet attrs) {
        int[] systemAttrs = {android.R.attr.layout_width, android.R.attr.layout_height};
        TypedArray array = context.obtainStyledAttributes(attrs, systemAttrs);

        TypedValue typedValue = new TypedValue();
        array.getValue(0, typedValue);
        if (typedValue.type != TypedValue.TYPE_DIMENSION) {
            return;
        }

        array.getValue(1, typedValue);
        if (typedValue.type != TypedValue.TYPE_DIMENSION) {
            return;
        }

        int width = array.getDimensionPixelOffset(0, 0);
        int height = array.getDimensionPixelOffset(1, 0);
        array.recycle();
        if (width <= 0 || height <= 0) { // auto-padding works in xml layout and the size must be exactly.
            return;
        }

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PaddingViewAttrs);
        int mContentWidth = a.getDimensionPixelOffset(R.styleable.PaddingViewAttrs_vp_content_width, 0);
        int mContentHeight = a.getDimensionPixelOffset(R.styleable.PaddingViewAttrs_vp_content_height, 0);
        int mContentPaddingLeft = a.getDimensionPixelOffset(R.styleable.PaddingViewAttrs_vp_content_padding_left, -1);
        int mContentPaddingTop = a.getDimensionPixelOffset(R.styleable.PaddingViewAttrs_vp_content_padding_top, -1);
        int mContentPaddingRight = a.getDimensionPixelOffset(R.styleable.PaddingViewAttrs_vp_content_padding_right, -1);
        int mContentPaddingBottom = a.getDimensionPixelOffset(R.styleable.PaddingViewAttrs_vp_content_padding_bottom, -1);
        a.recycle();

        if (mContentWidth <= 0 || mContentHeight <= 0 || mContentWidth > width || mContentHeight > height) {
            return;
        }

        int paddingLeft = (int) ((width - mContentWidth) / 2f + 0.5f); // compute the padding value which center the content
        int paddingRight = paddingLeft;
        int paddingTop = (int) ((height - mContentHeight) / 2f + 0.5f);
        int paddingBottom = paddingTop;

        // if having set the content padding, using the value first. if not, using the padding value which center the content
        if (mContentPaddingLeft >= 0) {
            paddingRight = paddingRight + paddingLeft - mContentPaddingLeft;
            paddingLeft = mContentPaddingLeft;
        } else if (mContentPaddingRight >= 0) {
            paddingLeft = paddingLeft + paddingRight - mContentPaddingRight;
            paddingRight = mContentPaddingRight;
        }

        if (mContentPaddingTop >= 0) {
            paddingBottom = paddingBottom + paddingTop - mContentPaddingTop;
            paddingTop = mContentPaddingTop;
        } else if (mContentPaddingBottom >= 0) {
            paddingTop = paddingTop + paddingBottom - mContentPaddingBottom;
            paddingBottom = mContentPaddingBottom;
        }

        if (paddingLeft != view.getPaddingLeft() || paddingTop != view.getPaddingTop()
                || paddingRight != view.getPaddingRight() || paddingBottom != view.getPaddingBottom()) {
            view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }

    }
}
