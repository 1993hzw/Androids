package cn.forward.androids.utils;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AnimatorUtil {

    // 默认的TimeInterpolator,前后减速，中间加速
    private static final TimeInterpolator sDefaultInterpolator =
            new AccelerateDecelerateInterpolator();

    public static AnimatorSetWrap createAnimator() {
        return new AnimatorSetWrap();
    }

    /**
     * @param interpolator 默认的TimeInterpolator
     * @return
     */
    public static AnimatorSetWrap createAnimator(TimeInterpolator interpolator) {
        return new AnimatorSetWrap(interpolator);
    }

    /**
     * @author huangziwei
     *         <p>
     *         对AnimatorSet进行封装，便以链式构建动画
     * @date 2016.3.8
     * @return
     */
    public static class AnimatorSetWrap {

        private View mView;
        public static final long DEFAULT_DURATION = 300; // ms

        // 使用then()方法执行的动画
        private ArrayList<Animator> mAnimatorsThen = new ArrayList<Animator>();

        private AnimatorSet mAnimatorSet;
        private AnimatorSet.Builder mAnimatorSetBuilder;
        private TimeInterpolator mTimeInterpolator;
        private boolean mIsPlaying = false; // 是否调用过play()方法，防止多次调用
        private boolean mHasInitThenAnim = false; //是否已经初始化then动画

        private int mRepeatCount = 0; // 重复次数,小于０则无限循环
        private int mCurrentRepeat; // 当前已经的重复次数
        private boolean mIsCanceled; // 是否已经取消

        public AnimatorSetWrap() {
            this(sDefaultInterpolator);
        }

        public AnimatorSetWrap(TimeInterpolator interpolator) {
            mRepeatCount = 0;
            mAnimatorSet = new AnimatorSet();
            mTimeInterpolator = interpolator;
        }

        public AnimatorSetWrap setRepeatCount(int count) {
            mRepeatCount = count;
            return this;
        }

        public int getRepeatCount() {
            return mRepeatCount;
        }

        public AnimatorSetWrap play(View view, String propertyName,
                                    float... values) {
            return play(view, DEFAULT_DURATION, null, mTimeInterpolator, propertyName, values);
        }

        public AnimatorSetWrap play(View view, long duration,
                                    String propertyName, float... values) {
            return play(view, duration, null, mTimeInterpolator, propertyName, values);
        }

        public AnimatorSetWrap play(View view, long duration, AnimatorListener listener, String propertyName, float... values) {
            return play(view, duration, listener, mTimeInterpolator, propertyName, values);
        }

        public AnimatorSetWrap play(View view, long duration, AnimatorListener listener,
                                    TimeInterpolator interpolator, String propertyName, float... values) {

            if (mIsPlaying) {
                throw new RuntimeException("AnimatorSetWrap.play()方法只能调用一次");
            }
            if (view == null) {
                throw new RuntimeException("view 不能为空");
            }

            mIsPlaying = true;
            mView = view;

            ObjectAnimator animator = ObjectAnimator.ofFloat(view,
                    propertyName, values).setDuration(duration);
            animator.setInterpolator(interpolator);
            if (listener != null) {
                animator.addListener(listener);
            }
            // 清空
            mAnimatorsThen.clear();
            mAnimatorSetBuilder = mAnimatorSet.play(animator);
            return this;
        }

        public AnimatorSetWrap play(Animator animator) {
            mAnimatorSetBuilder = mAnimatorSet.play(animator);
            return this;
        }

        public AnimatorSetWrap play(AnimatorSetWrap animator) {
            mAnimatorSetBuilder = mAnimatorSet.play(animator.getAnimatorSet());
            return this;
        }

        public AnimatorSetWrap with(View view, String propertyName,
                                    float... values) {
            return with(view, DEFAULT_DURATION, null, mTimeInterpolator, propertyName, values);
        }

        public AnimatorSetWrap with(View view, long duration,
                                    String propertyName, float... values) {
            return with(view, duration, null, mTimeInterpolator, propertyName, values);
        }

        public AnimatorSetWrap with(View view, long duration, AnimatorListener listener, String propertyName, float... values) {
            return with(view, duration, listener, mTimeInterpolator, propertyName, values);
        }

        public AnimatorSetWrap with(View view, long duration,
                                    AnimatorListener listener, TimeInterpolator interpolator, String propertyName, float... values) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(view,
                    propertyName, values).setDuration(duration);
            animator.setInterpolator(interpolator);
            if (listener != null) {
                animator.addListener(listener);
            }
            mAnimatorSetBuilder = mAnimatorSetBuilder.with(animator);
            return this;
        }

        public AnimatorSetWrap with(Animator animator) {
            mAnimatorSetBuilder = mAnimatorSetBuilder.with(animator);
            return this;
        }

        public AnimatorSetWrap with(AnimatorSetWrap animator) {
            mAnimatorSetBuilder = mAnimatorSetBuilder.with(animator.getAnimatorSet());
            return this;
        }

        public AnimatorSetWrap before(View view, String propertyName,
                                      float... values) {
            return before(view, DEFAULT_DURATION, null, mTimeInterpolator, propertyName, values);
        }

        public AnimatorSetWrap before(View view, long duration,
                                      String propertyName, float... values) {
            return before(view, duration, null, mTimeInterpolator, propertyName, values);
        }

        public AnimatorSetWrap before(View view, long duration,
                                      AnimatorListener listener, String propertyName, float... values) {
            return before(view, duration, listener, mTimeInterpolator, propertyName, values);
        }

        public AnimatorSetWrap before(View view, long duration,
                                      AnimatorListener listener, TimeInterpolator interpolator, String propertyName, float... values) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(view,
                    propertyName, values).setDuration(duration);
            animator.setInterpolator(interpolator);
            if (listener != null) {
                animator.addListener(listener);
            }
            mAnimatorSetBuilder = mAnimatorSetBuilder.before(animator);
            return this;
        }


        public AnimatorSetWrap before(Animator animator) {
            mAnimatorSetBuilder = mAnimatorSetBuilder.before(animator);
            return this;
        }

        public AnimatorSetWrap before(AnimatorSetWrap animator) {
            mAnimatorSetBuilder = mAnimatorSetBuilder.before(animator.getAnimatorSet());
            return this;
        }

        public AnimatorSetWrap after(View view, String propertyName,
                                     float... values) {
            return after(view, DEFAULT_DURATION, null, mTimeInterpolator, propertyName, values);
        }

        public AnimatorSetWrap after(View view, long duration,
                                     String propertyName, float... values) {
            return after(view, duration, null, mTimeInterpolator, propertyName, values);
        }

        public AnimatorSetWrap after(View view, long duration,
                                     AnimatorListener listener, String propertyName, float... values) {
            return after(view, duration, listener, mTimeInterpolator, propertyName, values);
        }

        public AnimatorSetWrap after(View view, long duration,
                                     AnimatorListener listener, TimeInterpolator interpolator, String propertyName, float... values) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(view,
                    propertyName, values).setDuration(duration);
            animator.setInterpolator(interpolator);
            if (listener != null) {
                animator.addListener(listener);
            }
            mAnimatorSetBuilder = mAnimatorSetBuilder.after(animator);
            return this;
        }


        public AnimatorSetWrap after(Animator animator) {
            mAnimatorSetBuilder = mAnimatorSetBuilder.after(animator);
            return this;
        }

        public AnimatorSetWrap after(AnimatorSetWrap animator) {
            mAnimatorSetBuilder = mAnimatorSetBuilder.after(animator.getAnimatorSet());
            return this;
        }


        public AnimatorSetWrap after(long delay) {
            mAnimatorSetBuilder.after(delay);
            return this;
        }

        public AnimatorSetWrap then(View view, String propertyName,
                                    float... values) {
            return then(view, DEFAULT_DURATION, null, mTimeInterpolator, propertyName, values);
        }

        public AnimatorSetWrap then(View view, long duration,
                                    String propertyName, float... values) {
            return then(view, duration, null, mTimeInterpolator, propertyName, values);
        }

        public AnimatorSetWrap then(View view, long duration,
                                    AnimatorListener listener, String propertyName, float... values) {
            return then(view, duration, listener, mTimeInterpolator, propertyName, values);
        }

        /**
         * 　play()方法的动画执行完成后，执行第一个then()方法的动画，接着执行下一个then()方法的动画，依次执行
         */
        public AnimatorSetWrap then(View view, long duration,
                                    AnimatorListener listener, TimeInterpolator interpolator, String propertyName, float... values) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(view,
                    propertyName, values).setDuration(duration);
            animator.setInterpolator(interpolator);
            if (listener != null) {
                animator.addListener(listener);
            }
            then(animator);
            return this;
        }

        public AnimatorSetWrap then(Animator animator) {
            mAnimatorsThen.add(animator);
            return this;
        }

        public AnimatorSetWrap then(AnimatorSetWrap animator) {
            mAnimatorsThen.add(animator.getAnimatorSet());
            return this;
        }

        private ScheduledExecutorService mRepeatSchedule;

        private void shutdownRepeat() {
            if (mRepeatSchedule != null) {
                try {
                    mRepeatSchedule.shutdownNow();
                    mRepeatSchedule = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleRepeat() {
            shutdownRepeat();
            mCurrentRepeat = 0;
            if (mRepeatCount == 0) {
                return;
            }
            // 重复执行动画
            mAnimatorSet.addListener(new AnimatorListenerAdapter() {
                long mStart, mEnd;

                @Override
                public void onAnimationStart(Animator animation) {
                    mStart = System.currentTimeMillis(); // 记录第一次执行所需的时间，以此确定调度器执行动画的间隔时间
                }

                @Override
                public void onAnimationEnd(final Animator animation) { // 在第一次执行结束时，加入调度器，实现重复执行动画
                    mEnd = System.currentTimeMillis();
                    mAnimatorSet.removeListener(this);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                        // 在ｍＶｉｅｗ移除时，停止调度器，避免内存溢出
                        mView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                            public void onViewAttachedToWindow(View v) {
                            }

                            public void onViewDetachedFromWindow(View v) {
                                cancel();
                            }
                        });
                    }

                    repeat();
                }

                private void repeat() {
                    if (mView == null || mIsCanceled) {
                        shutdownRepeat();
                        return;
                    }
                    // 切记：放到ＵＩ线程执行动画
                    mRepeatSchedule = Executors.newSingleThreadScheduledExecutor();
                    mRepeatSchedule.scheduleAtFixedRate(new Runnable() {
                        @Override
                        public void run() {
                            LogUtil.d("hzw", "animator schedule");

                            if (mIsCanceled) {
                                shutdownRepeat();
                                return;
                            }

                            if (!isVisibleOnScreen(mView)) { // 不可见，则暂停调度
                                shutdownRepeat();
                                mView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                                    @Override
                                    public boolean onPreDraw() {
                                        if (isVisibleOnScreen(mView)) { // 再次可见，则重新开启调度
                                            mView.getViewTreeObserver().removeOnPreDrawListener(this);
                                            repeat();
                                        }
                                        return true;
                                    }
                                });
                                return;
                            }

                            mView.post(new Runnable() {
                                public void run() {
                                    mAnimatorSet.cancel();
                                    mAnimatorSet.start();
                                }
                            });

                            if (mRepeatCount > 0) { // 判断是否完成重复次数
                                mCurrentRepeat++;
                                if (mCurrentRepeat == mRepeatCount) {
                                    shutdownRepeat();
                                }
                            }
                        }
                    }, 0, mEnd - mStart, TimeUnit.MILLISECONDS);
                }
            });
        }

        private void beforeStart() {

            mIsCanceled = false;
            handleRepeat();

            if (mHasInitThenAnim) { // 只在第一次启动时初始化
                return;
            }
            mHasInitThenAnim = true;
            if (mAnimatorsThen.size() > 0) {
                AnimatorSet set = new AnimatorSet();
                set.playSequentially(mAnimatorsThen);
                mAnimatorSetBuilder.before(set);
            }
        }

        public void start() {
            beforeStart();
            mAnimatorSet.start();
        }

        public void start(long duration) {
            beforeStart();
            mAnimatorSet.setDuration(duration);
            mAnimatorSet.start();
        }

        public void startDelay(long delay) {
            beforeStart();
            mAnimatorSet.setStartDelay(delay);
            mAnimatorSet.start();
        }

        public void cancel() {
            mIsCanceled = true;
            shutdownRepeat(); // 这时可能未开始重复执行动画，故无法取消调度器，因此必须通过mIsCanceled来判断是否取消
            mAnimatorSet.cancel();
            mCurrentRepeat = Integer.MAX_VALUE;
        }

        private AnimatorSet getAnimatorSet() {
            return mAnimatorSet;
        }

        public AnimatorSetWrap setListener(AnimatorListener listener) {
            mAnimatorSet.addListener(listener);
            return this;
        }

        public ArrayList<AnimatorListener> getListeners() {
            return mAnimatorSet.getListeners();
        }

        public void removeListner(AnimatorListener listener) {
            mAnimatorSet.removeListener(listener);
        }

        public void removeAllListeners() {
            mAnimatorSet.removeAllListeners();
        }
    }

    /**
     * 判断一个Ｖｉｅｗ　是否在当前的屏幕中可见（肉眼真实可见）
     *
     * @param mView
     * @return 返回true则可见
     */
    public static boolean isVisibleOnScreen(View mView) {
        if (mView == null) {
            return false;
        }
//         && mView.hasWindowFocus()　该方法会在当前页面上覆盖了另一个Ｄｉａｌｏｇ时返回false
        return mView.getWindowVisibility() == View.VISIBLE
                && mView.getVisibility() == View.VISIBLE && mView.isShown();
    }
}
