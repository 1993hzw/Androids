package cn.forward.androids.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * 直接在布局文件设置字体颜色遮罩和背景selector
 * Created by Administrator on 2017/1/1.
 */

public class SFrameLayout extends FrameLayout {


    public SFrameLayout(Context context) {
        this(context, null);
    }

    public SFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    private void init(AttributeSet attrs) {

        SelectorAttrs.obtainsAttrs(getContext(), this, attrs);
    }

}