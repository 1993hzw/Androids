package cn.forward.androids.views.behavior;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.math.MathUtils;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.OverScroller;

/**
 * 利用CoordinatorLayout+Behavior实现类似微信首页的弹性滑动和惯性滑动效果
 * @author ziwei huang
 */
public class OverScrollBehavior extends CoordinatorLayout.Behavior<View> {

    private static final int MAX_BOUNCE_BACK_DURATION_MS = 300;
    private static final int MIN_BOUNCE_BACK_DURATION_MS = 150;

    private final Interpolator mBounceBackInterpolator = new DecelerateInterpolator(0.8f);

    private ValueAnimator mSpringBackAnimator;
    private OverScroller mOverScroller;

    public OverScrollBehavior() {
    }

    public OverScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, View child,
                                       View directTargetChild, View target, int nestedScrollAxes, int type) {
        IOverScrollListener overscrollListener = (IOverScrollListener) child;

        if (type == ViewCompat.TYPE_TOUCH) {
            stopSpringBack();
        }

        if (!overscrollListener.canScroll(child, getOffset(child), IOverScrollListener.DIRECTION_DOWN)
                && !overscrollListener.canScroll(child, getOffset(child), IOverScrollListener.DIRECTION_UP)) {
            return false;
        }

        boolean start = (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
        if (start) {
            if (mOverScroller == null) {
                mOverScroller = new OverScroller(parent.getContext());
            }

            if (type == ViewCompat.TYPE_TOUCH) {
                mOverScroller.forceFinished(true);
            }
        }

        return start;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child,
                                  View target, int dx, int dy, int[] consumed, int type) {
        IOverScrollListener overscrollListener = (IOverScrollListener) child;

        if (dy != 0) {
            int min, max;
            if (dy < 0) {
                if (!overscrollListener.canScroll(child, getOffset(child), IOverScrollListener.DIRECTION_DOWN)) {
                    return;
                }

                // We're scrolling down
                min = getOffset(child);
                max = 0;
            } else {
                if (!overscrollListener.canScroll(child, getOffset(child), IOverScrollListener.DIRECTION_UP)) {
                    return;
                }

                // We're scrolling up
                min = 0;
                max = getOffset(child);
            }
            if (min != max) {
                consumed[1] = scrollWithoutDampingFactor(child, dy, min, max);
            }

        }
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, View child,
                               View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed,
                               int type) {
        IOverScrollListener overscrollListener = (IOverScrollListener) child;

        if (dyUnconsumed < 0) {
            // If the scrolling view is scrolling down but not consuming, it's probably be at
            // the top of it's content

            if (!overscrollListener.canScroll(child, getOffset(child), IOverScrollListener.DIRECTION_DOWN)) {
                return;
            }

            if (type == ViewCompat.TYPE_TOUCH) {
                scroll(child, dyUnconsumed,
                        0, child.getHeight());
            } else { // fling
                if ((mOverScroller.computeScrollOffset()
                        && Math.abs(mOverScroller.getCurrVelocity()) < overscrollListener.getMinFlingVelocity(child, getOffset(child), IOverScrollListener.DIRECTION_DOWN))  // too slow
                        || getOffset(child) >= overscrollListener.getMaxFlingOffset(child, getOffset(child), IOverScrollListener.DIRECTION_DOWN)) { // reach edge
                    ViewCompat.stopNestedScroll(target, ViewCompat.TYPE_NON_TOUCH);
                } else {
                    scroll(child, dyUnconsumed, // slow down
                            getOffset(child), overscrollListener.getMaxFlingOffset(child, getOffset(child), IOverScrollListener.DIRECTION_DOWN));
                }
            }

        } else if (dyUnconsumed > 0) {
            if (!overscrollListener.canScroll(child, getOffset(child), IOverScrollListener.DIRECTION_UP)) {
                return;
            }

            if (type == ViewCompat.TYPE_TOUCH) {
                scroll(child, dyUnconsumed,
                        -child.getHeight(), 0);
            } else { // fling
                if ((mOverScroller.computeScrollOffset()
                        && Math.abs(mOverScroller.getCurrVelocity()) < overscrollListener.getMinFlingVelocity(child, getOffset(child), IOverScrollListener.DIRECTION_UP)) // too slow
                        || getOffset(child) <= overscrollListener.getMaxFlingOffset(child, getOffset(child), IOverScrollListener.DIRECTION_UP)) { // reach edge
                    ViewCompat.stopNestedScroll(target, ViewCompat.TYPE_NON_TOUCH);
                } else {
                    scroll(child, dyUnconsumed,  // slow down
                            overscrollListener.getMaxFlingOffset(child, getOffset(child), IOverScrollListener.DIRECTION_UP), getOffset(child));
                }
            }
        }

    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, View child, View target, float velocityX, float velocityY) {
        if (child == target) {
            mOverScroller.fling(0, 0, 0, (int) velocityY, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        }
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, View child,
                                   View target, int type) {
        if (type == ViewCompat.TYPE_TOUCH) { // touching
            if (getOffset(child) != 0) { // and out of bound
                ViewCompat.stopNestedScroll(target, ViewCompat.TYPE_NON_TOUCH);
                springBack(child);
            }
        } else {
            springBack(child);
        }
    }

    /**
     *
     */
    private final int computerWithDampingFactor(View child, int dy) {
        IOverScrollListener overscroll = (IOverScrollListener) child;
        int direction = dy > 0 ? IOverScrollListener.DIRECTION_UP : IOverScrollListener.DIRECTION_DOWN;
        float factor = overscroll.getDampingFactor(child, getOffset(child), direction);
        if (factor == 0) {
            factor = 1;
        }
        int newDy = (int) (dy / factor + 0.5f);
        return newDy;
    }

    private final int scrollWithoutDampingFactor(View child, int dy, int minOffset, int maxOffset) {
        return computerOffset(child, getOffset(child) - dy, minOffset, maxOffset);
    }

    private final int scroll(View child, int dy, int minOffset, int maxOffset) {
        return computerOffset(child, getOffset(child) - computerWithDampingFactor(child, dy), minOffset, maxOffset);
    }

    /**
     *
     * @return 消耗掉距离
     */
    private int computerOffset(View child, int newOffset,
                               int minOffset, int maxOffset) {
        final int curOffset = getOffset(child);
        int consumed = 0;

        if (curOffset >= minOffset && curOffset <= maxOffset) {
            // If we have some scrolling range, and we're currently within the min and max
            // offsets, calculate a new offset
            newOffset = MathUtils.clamp(newOffset, minOffset, maxOffset);

            if (curOffset != newOffset) {
                setOffset(child, newOffset);
                // Update how much dy we have consumed
                consumed = curOffset - newOffset;
            }
        }

        return consumed;
    }

    private void setOffset(View child, int offset) {
        IOverScrollListener overscrollListener = (IOverScrollListener) child;
        child.setTranslationY(offset);
        overscrollListener.onOffsetChanged(child, (int) child.getTranslationY());
    }

    private int getOffset(View child) {
        return (int) child.getTranslationY();
    }

    private void stopSpringBack() {
        if (mSpringBackAnimator == null) {
            return;
        }
        if (mSpringBackAnimator.isRunning()) {
            mSpringBackAnimator.cancel();
        }
    }

    private void springBack(final View child) {

        int startOffset = getOffset(child);
        if (startOffset == 0) {
            return;
        }

        if (mSpringBackAnimator == null) {
            mSpringBackAnimator = ValueAnimator.ofInt();
            mSpringBackAnimator.setInterpolator(mBounceBackInterpolator);
        }

        if (mSpringBackAnimator.isStarted()) {
            return;
        }

        // Duration is proportional to the view's size.
        float bounceBackDuration = (Math.abs(startOffset) * 1f / child.getHeight()) * MAX_BOUNCE_BACK_DURATION_MS;
        mSpringBackAnimator.setIntValues(startOffset, 0);
        mSpringBackAnimator.setDuration(Math.max((int) bounceBackDuration, MIN_BOUNCE_BACK_DURATION_MS));
        mSpringBackAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                setOffset(child, value);
            }
        });
        mSpringBackAnimator.start();
    }

}