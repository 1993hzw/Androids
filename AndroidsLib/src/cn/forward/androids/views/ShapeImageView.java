package cn.forward.androids.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.graphics.Path.Direction;
import android.graphics.Path.FillType;
import android.util.AttributeSet;
import android.widget.ImageView;
import cn.forward.androids.R;

/**
 * 可设置形状的ImageView，抗边缘锯齿
 *
 * @author huangziwei
 */
public class ShapeImageView extends ImageView {

    public static int SHAPE_REC = 1; // 矩形
    public static int SHAPE_CIRCLE = 2; // 圆形

    private float mBorderSize = 0; // 边框大小,默认为０，即无边框
    private int mBorderColor = Color.WHITE; // 边框颜色，默认为白色
    private int mShape = SHAPE_REC; // 形状，默认为直接矩形
    private float mRoundRadius = 0; // 矩形的圆角半径,默认为０，即直角矩形
    private Path mPath = new Path();
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF mViewRect = new RectF(); // imageview的矩形区域
    private RectF mBorderRect = new RectF(); // 边框的矩形区域


    public ShapeImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShapeImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
        mPaint.setStyle(Style.STROKE);
        mPaint.setStrokeWidth(mBorderSize);
        mPaint.setColor(mBorderColor);
        mPaint.setAntiAlias(true);
    }

    private void init(AttributeSet attrs) {

        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.ShapeImageView);
        mShape = a.getInt(R.styleable.ShapeImageView_shape, mShape);
        mRoundRadius = a.getDimension(R.styleable.ShapeImageView_round_radius, mRoundRadius);
        mBorderSize = a.getDimension(R.styleable.ShapeImageView_border_size, mBorderSize);
        mBorderColor = a.getColor(R.styleable.ShapeImageView_border_color, mBorderColor);
        a.recycle();
    }

    /**
     *  对于普通的view,在执行到onDraw()时，背景图已绘制完成,所以必须在draw()中确定画布形状。
     *
     *  对于ViewGroup,当它没有背景时直接调用的是dispatchDraw()方法, 而绕过了draw()方法，
     *  当它有背景的时候就调用draw()方法，而draw()方法里包含了dispatchDraw()方法的调用，
     *  所以必须在dispatchDraw()中确定画布形状
     */
    @Override
    public void draw(Canvas canvas) {
        canvas.save(); //　保存canvas状态
        // 只绘制中间的几何部分
        canvas.clipPath(mPath);
        super.draw(canvas); // 绘制图片
        canvas.restore(); // 还原canvas状态
        // 一定要有canvas.save()和canvas.restore()，否则由于clipPath造成边缘出现锯齿，导致边框内侧平滑但外侧出现锯齿

        if (mBorderSize > 0) { // 绘制边框
            if (mShape == SHAPE_CIRCLE) {
                canvas.drawCircle(mViewRect.right / 2, mViewRect.bottom / 2,
                        Math.min(mViewRect.right, mViewRect.bottom) / 2 - mBorderSize / 2, mPaint);
            } else {
                canvas.drawRoundRect(mBorderRect, mRoundRadius, mRoundRadius, mPaint);
            }
        }
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 宽度高度已确定,获取矩形区域
        initShape();
    }

    //　设置图片的绘制区域
    private void initShape() {

        mViewRect.top = 0;
        mViewRect.left = 0;
        mViewRect.right = getWidth(); // 宽度
        mViewRect.bottom = getHeight(); // 高度

        // 边框的矩形区域不能等于ImageView的矩形区域，否则边框的宽度只显示了一半
        mBorderRect.top = mBorderSize / 2;
        mBorderRect.left = mBorderSize / 2;
        mBorderRect.right = getWidth() - mBorderSize / 2;
        mBorderRect.bottom = getHeight() - mBorderSize / 2;

        if (mShape == SHAPE_CIRCLE) { // 圆形
            float x = mViewRect.right / 2;
            float y = mViewRect.bottom / 2;
            // 半径取宽度和高度两者中的较小值的1/2，再减去mBorderSize/2是为了防止边框无法完全遮住图片边缘
            float r = Math.min(x, y) - mBorderSize / 2;
            mPath.reset();
            mPath.addCircle(x, y, r, Direction.CW);
            mPath.close();
            mPath.setFillType(FillType.WINDING);
        } else if (mShape == SHAPE_REC) { // 圆角矩形
            mPath.reset();
            mPath.addRoundRect(mBorderRect, mRoundRadius, mRoundRadius, Direction.CW);
            mPath.close();
            mPath.setFillType(FillType.WINDING);
        } else { // 为直角矩形，即原始的ImageView
            mPath.reset();
            mPath.addRect(mViewRect, Direction.CW);
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

    public float getBorderSize() {
        return mBorderSize;
    }

    public void setBorderSize(int mBorderSize) {
        this.mBorderSize = mBorderSize;
        mPaint.setStrokeWidth(mBorderSize);
        initShape();
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
        initShape();
    }

}
