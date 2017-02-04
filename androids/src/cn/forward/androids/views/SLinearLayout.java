package cn.forward.androids.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * 直接在布局文件设置字体颜色遮罩和背景selector
 * Created by Administrator on 2017/1/1.
 */

public class SLinearLayout extends LinearLayout {


    public SLinearLayout(Context context) {
        this(context, null);
    }

    public SLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    private void init(AttributeSet attrs) {

        SelectorAttrs.obtainsAttrs(getContext(), this, attrs);
    }

}