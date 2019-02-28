package com.example.androidsdemo.view;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;

import cn.forward.androids.views.behavior.IOverScrollListener;
import cn.forward.androids.views.behavior.OverScrollBehavior;

/**
 * @author ziwei huang
 */
@CoordinatorLayout.DefaultBehavior(OverScrollBehavior.class)
public class NestedOverScrollView extends NestedScrollView implements IOverScrollListener {
    public NestedOverScrollView(Context context) {
        super(context);
    }

    public NestedOverScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedOverScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean canScroll(View child, int verticalOffset, @ScrollDirection int scrollDirection) {
        return true;
    }


    @Override
    public int getMaxFlingOffset(View child, int verticalOffset, @ScrollDirection int scrollDirection) {
        if (scrollDirection == IOverScrollListener.DIRECTION_DOWN) {
            return getHeight() / 3;
        } else {
            return -getHeight() / 4;
        }
    }

    @Override
    public float getDampingFactor(View child, int verticalOffset, @ScrollDirection int scrollDirection) {
        if (scrollDirection == IOverScrollListener.DIRECTION_DOWN) {
            return 2;
        } else {
            return 3;
        }
    }

    @Override
    public int getMinFlingVelocity(View child, int verticalOffset, int scrollDirection) {
        return  ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity() * 20;
    }

    @Override
    public void onOffsetChanged(View child, int verticalOffset) {

    }
}
