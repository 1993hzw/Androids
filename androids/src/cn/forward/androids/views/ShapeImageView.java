package cn.forward.androids.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import cn.forward.androids.R;
import cn.forward.androids.utils.ImageUtils;

/**
 * 可设置形状的ImageView，抗边缘锯齿
 *
 * @author huangziwei
 */
public class ShapeImageView extends ImageView {

    public static int SHAPE_REC = 1; // 矩形
    public static int SHAPE_CIRCLE = 2; // 圆形
    public static int SHAPE_OVAL = 3; // 椭圆

    private float mBorderSize = 0; // 边框大小,默认为０，即无边框
    private int mBorderColor = Color.WHITE; // 边框颜色，默认为白色
    private int mShape = SHAPE_REC; // 形状，默认为直接矩形
    private float mRoundRadius = 0; // 矩形的圆角半径,默认为０，即直角矩形
    private float mRoundRadiusLeftTop, mRoundRadiusLeftBottom, mRoundRadiusRightTop, mRoundRadiusRightBottom;
    private Paint mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF mViewRect = new RectF(); // imageview的矩形区域
    private RectF mBorderRect = new RectF(); // 边框的矩形区域

    private final Matrix mShaderMatrix = new Matrix();
    private Paint mBitmapPaint = new Paint();
    private BitmapShader mBitmapShader;
    private Bitmap mBitmap;
    private Path mPath = new Path();

    public ShapeImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShapeImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle); // 虽然此处会调用setImageDrawable，但此时成员变量还未被正确初始化
        init(attrs);
        mBorderPaint.setStyle(Style.STROKE);
        mBorderPaint.setStrokeWidth(mBorderSize);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setAntiAlias(true);
        mBitmapPaint.setAntiAlias(true);
        super.setScaleType(ScaleType.CENTER_CROP); // 固定为CENTER_CROP，其他不生效
    }


    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        mBitmap = ImageUtils.getBitmapFromDrawable(getDrawable());
        setupBitmapShader();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mBitmap = ImageUtils.getBitmapFromDrawable(drawable);
        setupBitmapShader();
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType != ScaleType.CENTER_CROP) {
            throw new IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType));
        }
    }

    private void init(AttributeSet attrs) {

        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.ShapeImageView);
        mShape = a.getInt(R.styleable.ShapeImageView_siv_shape, mShape);
        mRoundRadius = a.getDimension(R.styleable.ShapeImageView_siv_round_radius, mRoundRadius);
        mBorderSize = a.getDimension(R.styleable.ShapeImageView_siv_border_size, mBorderSize);
        mBorderColor = a.getColor(R.styleable.ShapeImageView_siv_border_color, mBorderColor);

        mRoundRadiusLeftBottom = a.getDimension(R.styleable.ShapeImageView_siv_round_radius_leftBottom, mRoundRadius);
        mRoundRadiusLeftTop = a.getDimension(R.styleable.ShapeImageView_siv_round_radius_leftTop, mRoundRadius);
        mRoundRadiusRightBottom = a.getDimension(R.styleable.ShapeImageView_siv_round_radius_rightBottom, mRoundRadius);
        mRoundRadiusRightTop = a.getDimension(R.styleable.ShapeImageView_siv_round_radius_rightTop, mRoundRadius);

        a.recycle();
    }

    /**
     * 对于普通的view,在执行到onDraw()时，背景图已绘制完成
     * <p>
     * 对于ViewGroup,当它没有背景时直接调用的是dispatchDraw()方法, 而绕过了draw()方法，
     * 当它有背景的时候就调用draw()方法，而draw()方法里包含了dispatchDraw()方法的调用，
     */
    @Override
    public void onDraw(Canvas canvas) {

        if (mBitmap != null) {
            if (mShape == SHAPE_CIRCLE) {
                canvas.drawCircle(mViewRect.right / 2, mViewRect.bottom / 2,
                        Math.min(mViewRect.right, mViewRect.bottom) / 2, mBitmapPaint);
            } else if (mShape == SHAPE_OVAL) {
                canvas.drawOval(mViewRect, mBitmapPaint);
            } else {
//                canvas.drawRoundRect(mViewRect, mRoundRadius, mRoundRadius, mBitmapPaint);
                mPath.reset();
                mPath.addRoundRect(mViewRect, new float[]{
                        mRoundRadiusLeftTop, mRoundRadiusLeftTop,
                        mRoundRadiusRightTop, mRoundRadiusRightTop,
                        mRoundRadiusRightBottom, mRoundRadiusRightBottom,
                        mRoundRadiusLeftBottom, mRoundRadiusLeftBottom,
                }, Path.Direction.CW);
                canvas.drawPath(mPath, mBitmapPaint);

            }
        }

        if (mBorderSize > 0) { // 绘制边框
            if (mShape == SHAPE_CIRCLE) {
                canvas.drawCircle(mViewRect.right / 2, mViewRect.bottom / 2,
                        Math.min(mViewRect.right, mViewRect.bottom) / 2 - mBorderSize / 2, mBorderPaint);
            } else if (mShape == SHAPE_OVAL) {
                canvas.drawOval(mBorderRect, mBorderPaint);
            } else {
//                canvas.drawRoundRect(mBorderRect, mRoundRadius, mRoundRadius, mBorderPaint);
                mPath.reset();
                mPath.addRoundRect(mBorderRect, new float[]{
                        mRoundRadiusLeftTop, mRoundRadiusLeftTop,
                        mRoundRadiusRightTop, mRoundRadiusRightTop,
                        mRoundRadiusRightBottom, mRoundRadiusRightBottom,
                        mRoundRadiusLeftBottom, mRoundRadiusLeftBottom,
                }, Path.Direction.CW);
                canvas.drawPath(mPath, mBorderPaint);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initRect();
        setupBitmapShader();
    }

    // 不能在onLayout()调用invalidate()，否则导致绘制异常。（setupBitmapShader()中调用了invalidate()）
    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
//        initRect();
//        setupBitmapShader();
    }

    private void setupBitmapShader() {
        // super(context, attrs, defStyle)调用setImageDrawable时,成员变量还未被正确初始化
        if (mBitmapPaint == null) {
            return;
        }
        if (mBitmap == null) {
            invalidate();
            return;
        }
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mBitmapPaint.setShader(mBitmapShader);

        // 固定为CENTER_CROP,使图片在ｖｉｅｗ中居中并裁剪
        mShaderMatrix.set(null);
        // 缩放到高或宽　与view的高或宽　匹配
        float scale = Math.max(getWidth() * 1f / mBitmap.getWidth(), getHeight() * 1f / mBitmap.getHeight());
        // 由于BitmapShader默认是从画布的左上角开始绘制，所以把其平移到画布中间，即居中
        float dx = (getWidth() - mBitmap.getWidth() * scale) / 2;
        float dy = (getHeight() - mBitmap.getHeight() * scale) / 2;
        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate(dx, dy);
        mBitmapShader.setLocalMatrix(mShaderMatrix);
        invalidate();
    }

    //　设置图片的绘制区域
    private void initRect() {

        mViewRect.top = 0;
        mViewRect.left = 0;
        mViewRect.right = getWidth(); // 宽度
        mViewRect.bottom = getHeight(); // 高度

        // 边框的矩形区域不能等于ImageView的矩形区域，否则边框的宽度只显示了一半
        mBorderRect.top = mBorderSize / 2;
        mBorderRect.left = mBorderSize / 2;
        mBorderRect.right = getWidth() - mBorderSize / 2;
        mBorderRect.bottom = getHeight() - mBorderSize / 2;
    }

    public int getShape() {
        return mShape;
    }

    public void setShape(int shape) {
        mShape = shape;
    }

    public float getBorderSize() {
        return mBorderSize;
    }

    public void setBorderSize(int mBorderSize) {
        this.mBorderSize = mBorderSize;
        mBorderPaint.setStrokeWidth(mBorderSize);
        initRect();
        invalidate();
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(int mBorderColor) {
        this.mBorderColor = mBorderColor;
        mBorderPaint.setColor(mBorderColor);
        invalidate();
    }

    public float getRoundRadius() {
        return mRoundRadius;
    }

    public void setRoundRadius(float mRoundRadius) {
        this.mRoundRadius = mRoundRadius;
        invalidate();
    }

    public void setRoundRadiis(float roundRadiusLeftBottom, float roundRadiusLeftTop, float roundRadiusRightBottom, float roundRadiusRightTop) {
        mRoundRadiusLeftBottom = roundRadiusLeftBottom;
        mRoundRadiusLeftTop = roundRadiusLeftTop;
        mRoundRadiusRightBottom = roundRadiusRightBottom;
        mRoundRadiusRightTop = roundRadiusRightTop;
        invalidate();
    }

    public float[] getRoundRadiis() {
        return new float[]{mRoundRadiusLeftBottom, mRoundRadiusLeftTop, mRoundRadiusRightBottom, mRoundRadiusRightTop};
    }

}
