package cn.forward.androids.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 点击时，显示遮罩
 * 
 * @author huangziwei
 * @date 2015.12.29
 */
public class ShadeImageView extends RatioImageView {

	private boolean mIsShowShade = true; // 是否显示遮罩
	private boolean mIsPressed; // 是否正在点击

	private int mShadeColor = 0x88000000; // 遮罩颜色（argb）

	public ShadeImageView(Context context) {
		this(context, null);
	}

	public ShadeImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ShadeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.setClickable(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mIsShowShade && mIsPressed) {
			// 绘制遮罩层
			canvas.drawColor(mShadeColor);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (!mIsShowShade) {
			return super.onTouchEvent(event);
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mIsPressed = true;
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_OUTSIDE:
		case MotionEvent.ACTION_CANCEL:
			// 当手指离开imageview区域时，取消遮罩
			mIsPressed = false;
			invalidate();
			break;
		default:
			break;
		}
		// 不返回true，即不拦截触摸事件
		return super.onTouchEvent(event);
	}

	public boolean isIsShowShade() {
		return mIsShowShade;
	}

	public void setIsShowShade(boolean mIsShowShade) {
		this.mIsShowShade = mIsShowShade;
	}

}