package cn.forward.androids.utils;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;

public class AnimatorUtil {

	/**
	 * 
	 * @date 2016.3.8
	 * @author huangziwei
	 * 
	 *         对AnimatorSet进行封装，便以链式构建动画
	 * 
	 * @param obj
	 *            　要实现动画效果的对象
	 * @return
	 */
	public static AnimatorSetWrap createAnimator(Object obj) {
		return new AnimatorSetWrap(obj);
	}

	public static class AnimatorSetWrap {

		public static final long DURATION = 300; // ms,默认的动画时间

		// 使用then()方法执行的动画
		private ArrayList<Animator> mAnimatorsThen = new ArrayList<Animator>();

		private Object mObject;
		private AnimatorSet mAnimatorSet;
		private AnimatorSet.Builder mAnimatorSetBuilder;
		private boolean mIsPlaying = false; // 是否调用过play()方法，防止多次调用

		public AnimatorSetWrap(Object obj) {
			mObject = obj;
			mAnimatorSet = new AnimatorSet();
		}

		public AnimatorSetWrap play(long duration, String propertyName,
				float... values) {
			return play(mObject, duration, null, propertyName, values);
		}

		public AnimatorSetWrap play(long duration, AnimatorListener listener,
				String propertyName, float... values) {
			return play(mObject, duration, listener, propertyName, values);
		}

		public AnimatorSetWrap play(Object obj, long duration,
				String propertyName, float... values) {
			return play(obj, duration, null, propertyName, values);
		}

		/**
		 * {@link cn.forward.androids.utils.AnimatorUtil.AnimatorSetWrap#then(java.lang.Object  obj, long duration, android.animation.Animator.AnimatorListener listener, String propertyName, float... values)}
		 */
		public AnimatorSetWrap play(Object obj, long duration,
				AnimatorListener listener, String propertyName, float... values) {

			if (mIsPlaying) {
				throw new RuntimeException("AnimatorSetWrap.play()方法只能调用一次");
			}
			mIsPlaying = true;

			ObjectAnimator animator = ObjectAnimator.ofFloat(obj,
					propertyName, values).setDuration(duration);
			if (listener != null) {
				animator.addListener(listener);
			}
			mAnimatorsThen.clear(); // 清空
			animator.addListener(new AnimatorListenerThen( // 动画执行完之后执行第一个then()动画
					mAnimatorsThen.size()));
			mAnimatorSetBuilder = mAnimatorSet.play(animator);
			return this;
		}

		public AnimatorSetWrap play(Animator animator) {
			mAnimatorSetBuilder = mAnimatorSet.play(animator);
			return this;
		}

		public AnimatorSetWrap with(Object obj, long duration,
				String propertyName, float... values) {
			return with(obj, duration, null, propertyName, values);
		}

		public AnimatorSetWrap with(Object obj, long duration,
				AnimatorListener listener, String propertyName, float... values) {
			ObjectAnimator animator = ObjectAnimator.ofFloat(obj,
					propertyName, values).setDuration(duration);
			if (listener != null) {
				animator.addListener(listener);
			}
			mAnimatorSetBuilder = mAnimatorSetBuilder.with(animator);
			return this;
		}

		public AnimatorSetWrap with(long duration, String propertyName,
				float... values) {
			return with(mObject, duration, null, propertyName, values);
		}

		public AnimatorSetWrap with(long duration, AnimatorListener listener,
				String propertyName, float... values) {
			return with(mObject, duration, listener, propertyName, values);
		}

		public AnimatorSetWrap with(Animator animator) {
			mAnimatorSetBuilder = mAnimatorSetBuilder.with(animator);
			return this;
		}

		public AnimatorSetWrap before(Object obj, long duration,
				String propertyName, float... values) {
			return before(obj, duration, null, propertyName, values);
		}

		public AnimatorSetWrap before(Object obj, long duration,
				AnimatorListener listener, String propertyName, float... values) {
			ObjectAnimator animator = ObjectAnimator.ofFloat(obj,
					propertyName, values).setDuration(duration);
			if (listener != null) {
				animator.addListener(listener);
			}
			mAnimatorSetBuilder = mAnimatorSetBuilder.before(animator);
			return this;
		}

		public AnimatorSetWrap before(long duration, String propertyName,
				float... values) {
			return before(mObject, duration, null, propertyName, values);
		}

		public AnimatorSetWrap before(long duration, AnimatorListener listener,
				String propertyName, float... values) {
			return before(mObject, duration, listener, propertyName, values);
		}

		public AnimatorSetWrap before(Animator animator) {
			mAnimatorSetBuilder = mAnimatorSetBuilder.before(animator);
			return this;
		}

		public AnimatorSetWrap after(Object obj, long duration,
				String propertyName, float... values) {
			return after(obj, duration, null, propertyName, values);
		}

		public AnimatorSetWrap after(Object obj, long duration,
				AnimatorListener listener, String propertyName, float... values) {
			ObjectAnimator animator = ObjectAnimator.ofFloat(obj,
					propertyName, values).setDuration(duration);
			if (listener != null) {
				animator.addListener(listener);
			}
			mAnimatorSetBuilder = mAnimatorSetBuilder.after(animator);
			return this;
		}

		public AnimatorSetWrap after(long duration, String propertyName,
				float... values) {
			return after(mObject, duration, null, propertyName, values);
		}

		public AnimatorSetWrap after(long duration, AnimatorListener listener,
				String propertyName, float... values) {
			return after(mObject, duration, listener, propertyName, values);
		}

		public AnimatorSetWrap after(Animator animator) {
			mAnimatorSetBuilder = mAnimatorSetBuilder.after(animator);
			return this;
		}

		public AnimatorSetWrap after(long delay) {
			mAnimatorSetBuilder.after(delay);
			return this;
		}

		public void start() {
			mAnimatorSet.start();
		}

		public void start(long duration) {
			mAnimatorSet.setDuration(duration);
			mAnimatorSet.start();
		}

		public void startDelay(long delay) {
			mAnimatorSet.setStartDelay(delay);
			mAnimatorSet.start();
		}

		public AnimatorSet getAnimatorSet() {
			return mAnimatorSet;
		}

		public AnimatorSetWrap setListener(AnimatorListener listener) {
			mAnimatorSet.addListener(listener);
			return this;
		}

		public ArrayList<AnimatorListener> getListeners() {
			return mAnimatorSet.getListeners();
		}

		/**
		 * {@link cn.forward.androids.utils.AnimatorUtil.AnimatorSetWrap#then(java.lang.Object obj, long duration, android.animation.Animator.AnimatorListener listener, String propertyName, float... values)}
		 */
		public AnimatorSetWrap then(Object obj, long duration,
				String propertyName, float... values) {
			return then(obj, duration, null, propertyName, values);
		}

		/**
		 * 　play()方法的动画执行完成后，执行第一个then()方法的动画，接着执行下一个then()方法的动画，依次执行
		 * 
		 */
		public AnimatorSetWrap then(Object obj, long duration,
				AnimatorListener listener, String propertyName, float... values) {
			ObjectAnimator animator = ObjectAnimator.ofFloat(obj,
					propertyName, values).setDuration(duration);
			if (listener != null) {
				animator.addListener(listener);
			}
			mAnimatorsThen.add(animator);
			animator.addListener(new AnimatorListenerThen(mAnimatorsThen.size()));
			return this;
		}


		/**
		 * {@link cn.forward.androids.utils.AnimatorUtil.AnimatorSetWrap#then(java.lang.Object  obj, long duration, android.animation.Animator.AnimatorListener listener, String propertyName, float... values)}
		 */
		public AnimatorSetWrap then(long duration, String propertyName,
				float... values) {
			return then(mObject, duration, null, propertyName, values);
		}

		/**
		 * {@link cn.forward.androids.utils.AnimatorUtil.AnimatorSetWrap#then(java.lang.Object  obj, long duration, android.animation.Animator.AnimatorListener listener, String propertyName, float... values)}
		 */
		public AnimatorSetWrap then(long duration, AnimatorListener listener,
				String propertyName, float... values) {
			return then(mObject, duration, listener, propertyName, values);
		}

		/**
		 * {@link cn.forward.androids.utils.AnimatorUtil.AnimatorSetWrap#then(java.lang.Object  obj, long duration, android.animation.Animator.AnimatorListener listener, String propertyName, float... values)}
		 */
		public AnimatorSetWrap then(Animator animator) {
			mAnimatorsThen.add(animator);
			animator.addListener(new AnimatorListenerThen(mAnimatorsThen.size()));
			return this;
		}

		/**
		 * 
		 * 使用then()方法时添加listener，目的是为了动画执行完成后，接着执行下一个then()方法对应的动画
		 * 
		 * @author huangziwei
		 * 
		 */
		private class AnimatorListenerThen implements AnimatorListener {

			private int mNextAnim = -1; // 下一个动画在mAnimators中的索引

			public AnimatorListenerThen(int nextAnimIndex) {
				mNextAnim = nextAnimIndex;
			}

			public void onAnimationStart(Animator animation) {
			}

			public void onAnimationEnd(Animator animation) {
				// 执行下一个动画
				if (mNextAnim < mAnimatorsThen.size()) { 
					mAnimatorsThen.get(mNextAnim).start();
				}
			}

			public void onAnimationCancel(Animator animation) {
			}

			public void onAnimationRepeat(Animator animation) {
			}
		}

	}

	/**
	 * AnimatorListener实现类
	 * 
	 * @author huangziwei
	 * 
	 */
	public static class BaseAnimatorListener implements AnimatorListener {

		public BaseAnimatorListener() {
		}

		public void onAnimationStart(Animator animation) {
		}

		public void onAnimationEnd(Animator animation) {
		}

		public void onAnimationCancel(Animator animation) {
		}

		public void onAnimationRepeat(Animator animation) {
		}
	}

}
