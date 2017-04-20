package cn.forward.androids.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import cn.forward.androids.R;

/**
 * 直接在布局文件设置字体颜色遮罩和背景selector
 * Created by Administrator on 2017/1/1.
 */

public class STextView extends TextView {


    public STextView(Context context) {
        this(context, null);
    }

    public STextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public STextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.STextView);
        int defaultColor = getTextColors().getDefaultColor();
        int textColorSelected = a.getColor(R.styleable.STextView_stv_text_color_selected, defaultColor);
        int textColorPressed = a.getColor(R.styleable.STextView_stv_text_color_pressed, a.getColor(R.styleable.STextView_mtv_text_color_pressed, defaultColor));
        int textColorDisable = a.getColor(R.styleable.STextView_stv_text_color_disable, a.getColor(R.styleable.STextView_mtv_text_color_disable, defaultColor));
        setTextColor(createColorStateList(textColorPressed, textColorSelected, textColorDisable, defaultColor));
        a.recycle();

        SelectorAttrs.obtainsAttrs(getContext(), this, attrs);
    }

    private ColorStateList createColorStateList(int pressed, int selected, int unable, int normal) {
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{ // 遍历到满足的状态则停止遍历
                        {android.R.attr.state_enabled, android.R.attr.state_pressed},
                        {android.R.attr.state_enabled, android.R.attr.state_selected},
                        {-android.R.attr.state_enabled},
                        {},
                },
                new int[]{
                        pressed,
                        selected,
                        unable,
                        normal,
                });
        return colorStateList;
    }

}