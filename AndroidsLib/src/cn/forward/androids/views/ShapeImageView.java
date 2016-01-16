package cn.forward.androids.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Path.FillType;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 可设置形状的ImageView
 * 
 * @author huangziwei
 */
public class ShapeImageView extends ImageView {

	public static int SHAPE_REC = 1; // 矩形
	public static int SHAPE_CIRCLE = 2; // 圆形

	private int mBorderSize = 0; // 边框大小,默认为０，即无边框
	private int mBorderColor = Color.WHITE; // 边框颜色，默认为白色
	private int mShape = SHAPE_REC; // 形状，默认为直接矩形
	private float mRoundRadius = 0; // 矩形的圆角半径,默认为０，即直角矩形
	private Path mPath = new Path();
	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private RectF mRectF = new RectF(); // imageview的矩形区域

	public ShapeImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ShapeImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(mBorderSize);
		mPaint.setColor(mBorderColor);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// 只绘制中间的圆形部分
		canvas.clipPath(mPath);
		super.onDraw(canvas);
		if (mBorderSize > 0) { // 绘制边框
			if (mShape == SHAPE_CIRCLE) {
				canvas.drawCircle(mRectF.right / 2, mRectF.bottom / 2,
						Math.min(mRectF.right, mRectF.bottom) / 2, mPaint);
			} else {
				canvas.drawRoundRect(mRectF, mRoundRadius, mRoundRadius, mPaint);
			}
		}
	}

	// 宽度高度已确定，设置圆形
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		mRectF.top = 0;
		mRectF.left = 0;
		mRectF.right = getWidth(); // 宽度
		mRectF.bottom = getHeight(); // 高度
		initShape();
	}

	private void initShape() {
		if (mShape == SHAPE_CIRCLE) {
			int x = (int) (mRectF.right / 2);
			int y = (int) (mRectF.bottom / 2);
			int r = Math.min(x, y); // 半径取宽度和高度两者中的较小值的一半
			mPath.reset();
			mPath.addCircle(x, y, r, Direction.CW);
			mPath.close();
			mPath.setFillType(FillType.WINDING);
		} else if (mShape == SHAPE_REC) { // 圆角矩形
			mPath.reset();
			mPath.addRoundRect(mRectF, mRoundRadius, mRoundRadius, Direction.CW);
			mPath.close();
			mPath.setFillType(FillType.WINDING);
		} else { // 默认为直接矩形，即原始的ImageView
			mPath.reset();
			mPath.addRect(mRectF, Direction.CW);
			mPath.close();
			mPath.setFillType(FillType.WINDING);
		}
		invalidate();
	}

	public int getShape() {
		return mShape;
	}

	public void setShape(int shape) {
		mShape = shape;
		initShape();
	}

	public int getBorderSize() {
		return mBorderSize;
	}

	public void setBorderSize(int mBorderSize) {
		this.mBorderSize = mBorderSize;
		mPaint.setStrokeWidth(mBorderSize);
		invalidate();
	}

	public int getBorderColor() {
		return mBorderColor;
	}

	public void setBorderColor(int mBorderColor) {
		this.mBorderColor = mBorderColor;
		mPaint.setColor(mBorderColor);
		invalidate();
	}

	public float getRoundRadius() {
		return mRoundRadius;
	}

	public void setRoundRadius(float mRoundRadius) {
		this.mRoundRadius = mRoundRadius;
		invalidate();
	}

}
