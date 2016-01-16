package cn.forward.androids.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import cn.forward.androids.R;

/**
 * 可以设置宽度和高度的比例，
 * 
 * @author huangziwei
 * @date 2015.12.29
 */
public class RatioImageView extends ImageView {

	// 优先级从大到小：
	// mIsWidthFitDrawableSizeRatio mIsHeightFitDrawableSizeRatio
	// mWidthRatio mHeightRatio
	// 即如果设置了mIsWidthFitDrawableSizeRatio为true，则优先级较低的三个值不生效

	private boolean mIsWidthFitDrawableSizeRatio; // 宽度是否根据src图片的比例来测量（高度已知）
	private boolean mIsHeightFitDrawableSizeRatio = true; // 高度是否根据src图片的比例来测量（宽度已知）
	private float mWidthRatio; // 宽度 = 高度*mWidthRatio
	private float mHeightRatio; // 高度 = 宽度*mHeightRatio

	private float mDrawableSizeRatio = -1f; // src图片的宽高比例

	public RatioImageView(Context context) {
		this(context, null);
	}

	public RatioImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr); // 虽然此处会调用setImageDrawable，但此时成员变量还未被正确初始化
		init(attrs);
		// 一定要有此代码
		if (getDrawable() != null) {
			mDrawableSizeRatio = 1f * getDrawable().getIntrinsicWidth()
					/ getDrawable().getIntrinsicHeight();
		}
	}

	/**
	 * 初始化变量
	 */
	private void init(AttributeSet attrs) {

		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.PercentImageView);
		mHeightRatio = a.getFloat(
				R.styleable.PercentImageView_height_to_width_ratio, -1);
		mWidthRatio = a.getFloat(
				R.styleable.PercentImageView_width_to_height_ratio, -1);
		a.recycle();
	}

	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		if (getDrawable() != null) {
			mDrawableSizeRatio = 1f * getDrawable().getIntrinsicWidth()
					/ getDrawable().getIntrinsicHeight();
			if (mDrawableSizeRatio > 0
					&& (mIsWidthFitDrawableSizeRatio || mIsHeightFitDrawableSizeRatio)) {
				requestLayout();
			}
		}
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		super.setImageDrawable(drawable);
		if (getDrawable() != null) {
			mDrawableSizeRatio = 1f * getDrawable().getIntrinsicWidth()
					/ getDrawable().getIntrinsicHeight();
			if (mDrawableSizeRatio > 0
					&& (mIsWidthFitDrawableSizeRatio || mIsHeightFitDrawableSizeRatio)) {
				requestLayout();
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 优先级从大到小：
		// mIsWidthFitDrawableSizeRatio mIsHeightFitDrawableSizeRatio
		// mWidthRatio mHeightRatio
		if (mDrawableSizeRatio > 0) {
			if (mIsWidthFitDrawableSizeRatio) {
				mWidthRatio = mDrawableSizeRatio;
			} else if (mIsHeightFitDrawableSizeRatio) {
				mHeightRatio = 1 / mDrawableSizeRatio;
			}
		}

		if (mHeightRatio > 0 && mWidthRatio > 0) {
			throw new RuntimeException("高度和宽度不能同时设置百分比！！");
		}

		if (mWidthRatio > 0) { // 高度已知，根据比例，设置宽度
			int height = MeasureSpec.getSize(heightMeasureSpec);
			super.onMeasure(MeasureSpec.makeMeasureSpec(
					(int) (height * mWidthRatio), MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
		} else if (mHeightRatio > 0) { // 宽度已知，根据比例，设置高度
			int width = MeasureSpec.getSize(widthMeasureSpec);
			super.onMeasure(MeasureSpec.makeMeasureSpec(width,
					MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
					(int) (width * mHeightRatio), MeasureSpec.EXACTLY));
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	public boolean isIsWidthFitDrawableSizeRatio() {
		return mIsWidthFitDrawableSizeRatio;
	}

	public void setIsWidthFitDrawableSizeRatio(
			boolean mIsWidthFitDrawableSizeRatio) {
		this.mIsWidthFitDrawableSizeRatio = mIsWidthFitDrawableSizeRatio;
	}

	public boolean isIsHeightFitDrawableSizeRatio() {
		return mIsHeightFitDrawableSizeRatio;
	}

	public void setIsHeightFitDrawableSizeRatio(
			boolean mIsHeightFitDrawableSizeRatio) {
		this.mIsHeightFitDrawableSizeRatio = mIsHeightFitDrawableSizeRatio;
	}

	public float getWidthRatio() {
		return mWidthRatio;
	}

	public void setWidthRatio(float mWidthRatio) {
		this.mWidthRatio = mWidthRatio;
	}

	public float getHeightRatio() {
		return mHeightRatio;
	}

	public void setHeightRatio(float mHeightRatio) {
		this.mHeightRatio = mHeightRatio;
	}

	public float getDrawableSizeRatio() {
		return mDrawableSizeRatio;
	}

}
