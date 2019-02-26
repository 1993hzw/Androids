package cn.forward.androids.views.behavior;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.math.MathUtils;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.OverScroller;

/**
 * @author ziwei huang
 */
public class OverscrollBehavior extends CoordinatorLayout.Behavior<View> {

    private static final int MAX_BOUNCE_BACK_DURATION_MS = 350;
    private static final int MIN_BOUNCE_BACK_DURATION_MS = 200;

    private final Interpolator mBounceBackInterpolator = new DecelerateInterpolator(0.8f);
    private int mMinOverScrollVelocity = 0;

    private ValueAnimator mSpringBackAnimator;
    private OverScroller mOverScroller;

    public OverscrollBehavior() {
    }

    public OverscrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, View child, View target, float velocityX, float velocityY) {
        if (child == target) {

            mOverScroller.fling(0, 0, 0, (int) velocityY, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        }
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, View child,
                                       View directTargetChild, View target, int nestedScrollAxes, int type) {
        IOverscroll iOverscroll = (IOverscroll) child;

        if (!iOverscroll.canScrollDown() && !iOverscroll.canScrollUp()) {
            return false;
        }

        boolean start = (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
        if (start) {
            if (mMinOverScrollVelocity == 0) {
                mMinOverScrollVelocity = ViewConfiguration.get(parent.getContext()).getScaledMinimumFlingVelocity() * 30;
            }
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
        IOverscroll iOverscroll = (IOverscroll) child;
        if (type == ViewCompat.TYPE_TOUCH) {
            stopSpringBack();
        }

        if (dy != 0) {
            int min, max;
            if (dy < 0) {
                if (!iOverscroll.canScrollDown()) {
                    return;
                }

                // We're scrolling down
                min = getOffset(child);
                max = 0;
            } else {
                if (!iOverscroll.canScrollUp()) {
                    return;
                }

                // We're scrolling up
                min = 0;
                max = getOffset(child);
            }
            if (min != max) {
                consumed[1] = scrollWithoutDecelerate(child, dy, min, max);
            }

        }
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, View child,
                               View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed,
                               int type) {
        IOverscroll iOverscroll = (IOverscroll) child;

        if (dyUnconsumed < 0) {
            if (!iOverscroll.canScrollDown()) {
                return;
            }

            if (type == ViewCompat.TYPE_TOUCH) {
                // If the scrolling view is scrolling down but not consuming, it's probably be at
                // the top of it's content
                scroll(child, dyUnconsumed,
                        0, child.getHeight());
            } else { // fling
                if ((mOverScroller.computeScrollOffset()
                        && Math.abs(mOverScroller.getCurrVelocity()) < mMinOverScrollVelocity)  // too slow
                        || getOffset(child) >= iOverscroll.getMaxFlingScrollDown()) { // reach edge
                    ViewCompat.stopNestedScroll(target, ViewCompat.TYPE_NON_TOUCH);
                } else {
                    scroll(child, dyUnconsumed, // slow down
                            getOffset(child), iOverscroll.getMaxFlingScrollDown());
                }
            }

        } else if (dyUnconsumed > 0) {
            if (!iOverscroll.canScrollUp()) {
                return;
            }

            if (type == ViewCompat.TYPE_TOUCH) {
                scroll(child, dyUnconsumed,
                        -child.getHeight(), 0);
            } else { // fling
                if ((mOverScroller.computeScrollOffset()
                        && Math.abs(mOverScroller.getCurrVelocity()) < mMinOverScrollVelocity) // too slow
                        || getOffset(child) <= iOverscroll.getMaxFlingScrollUp()) { // reach edge
                    ViewCompat.stopNestedScroll(target, ViewCompat.TYPE_NON_TOUCH);
                } else {
                    scroll(child, dyUnconsumed,  // slow down
                            iOverscroll.getMaxFlingScrollUp(), getOffset(child));
                }
            }
        }

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
    private final int computerDecelerateDy(View view, int dy) {
        IOverscroll overscroll = (IOverscroll) view;
        int newDy = (int) (dy / overscroll.getDampingFactor() + 0.5f);
        return newDy;
    }

    private final int scrollWithoutDecelerate(View view, int dy, int minOffset, int maxOffset) {
        return computerOffset(view, getOffset(view) - dy, minOffset, maxOffset);
    }

    private final int scroll(View view, int dy, int minOffset, int maxOffset) {
        return computerOffset(view, getOffset(view) - computerDecelerateDy(view, dy), minOffset, maxOffset);
    }

    private int computerOffset(View view, int newOffset,
                               int minOffset, int maxOffset) {
        final int curOffset = getOffset(view);
        int consumed = 0;

        if (curOffset >= minOffset && curOffset <= maxOffset) {
            // If we have some scrolling range, and we're currently within the min and max
            // offsets, calculate a new offset
            newOffset = MathUtils.clamp(newOffset, minOffset, maxOffset);

            if (curOffset != newOffset) {
                setOffset(view, newOffset);
                // Update how much dy we have consumed
                consumed = curOffset - newOffset;
            }
        }

        return consumed;
    }

    private void setOffset(View view, int offset) {
        view.setTranslationY(offset);
    }

    private int getOffset(View view) {
        return (int) view.getTranslationY();
    }

    private void stopSpringBack() {
        if (mSpringBackAnimator == null) {
            return;
        }
        if (mSpringBackAnimator.isRunning()) {
            mSpringBackAnimator.cancel();
        }
    }

    private void springBack(final View view) {

        int startOffset = getOffset(view);
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
        float bounceBackDuration = (Math.abs(startOffset) * 1f / view.getHeight()) * MAX_BOUNCE_BACK_DURATION_MS;
        mSpringBackAnimator.setIntValues(startOffset, 0);
        mSpringBackAnimator.setDuration(Math.max((int) bounceBackDuration, MIN_BOUNCE_BACK_DURATION_MS));
        mSpringBackAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                setOffset(view, value);
            }
        });
        mSpringBackAnimator.start();
    }

}