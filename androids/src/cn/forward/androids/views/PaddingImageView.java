package cn.forward.androids.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 直接在xml布局文件设置ImageView的内容padding, 支持靠边对齐
 * layout_width/height必须为精确值。用于解决点击区域大于内容区域的问题。
 */

public class PaddingImageView extends ImageView {


    public PaddingImageView(Context context) {
        this(context, null);
    }

    public PaddingImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaddingImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    private void init(AttributeSet attrs) {
        PaddingViewAttrs.obtainsAttrs(getContext(), this, attrs);
    }
}