package com.example.androidsdemo.view;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;

import cn.forward.androids.views.behavior.IOverscroll;

/**
 * @author ziwei huang
 */
public class NestedOverscrollView extends NestedScrollView implements IOverscroll {
    public NestedOverscrollView(Context context) {
        super(context);
    }

    public NestedOverscrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedOverscrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean canScrollDown() {
        return true;
    }

    @Override
    public boolean canScrollUp() {
        return true;
    }

    @Override
    public int getMaxFlingScrollDown() {
        return getHeight() / 3;
    }

    @Override
    public int getMaxFlingScrollUp() {
        return -getHeight() / 3;
    }

    @Override
    public float getDampingFactor() {
        return 2;
    }
}
