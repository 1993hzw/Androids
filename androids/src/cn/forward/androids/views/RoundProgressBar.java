package cn.forward.androids.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import cn.forward.androids.R;

/**
 * 圆形进度条
 * Created by huangziwei on 16-7-11.
 */
public class RoundProgressBar extends View {

    private Paint mPaint;
    private RectF mRect;
    private float mProgress = 0;
    private float mMaxProgress = 100;
    private int mColor = 0xffffea5e;
    private int mWidth = 20;
    private int mBgWidth = 20;
    private int mBackground = 0;
    private RoundProgressBarListener mListener;


    public RoundProgressBar(Context context) {
        this(context, null, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
        mRect = new RectF();
    }

    private void init(AttributeSet attrs) {

        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.RoundProgressBar);
        mWidth = a.getDimensionPixelOffset(R.styleable.RoundProgressBar_rpb_width, mWidth);
        mColor = a.getColor(R.styleable.RoundProgressBar_rpb_color, mColor);
        mBackground = a.getColor(R.styleable.RoundProgressBar_rpb_background, mBackground);
        mProgress = a.getFloat(R.styleable.RoundProgressBar_rpb_progress, mProgress);
        mMaxProgress = a.getFloat(R.styleable.RoundProgressBar_rpb_max_progress, mMaxProgress);
        mBgWidth = a.getDimensionPixelOffset(R.styleable.RoundProgressBar_rpb_background_width, mBgWidth);
        a.recycle();
    }

    public void setProgress(float progress) {
        mProgress = progress < 0 ? 0 : progress;
        if (mListener != null) {
            mListener.onProgressChanged(mProgress);
        }
        invalidate();
    }

    public float getProgress() {
        return mProgress;
    }

    public void setMaxProgress(float maxProgress) {
        mMaxProgress = maxProgress < 0 ? 100 : mMaxProgress;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRect.left = getPaddingLeft() + mWidth / 2;
        mRect.top = getPaddingTop() + mWidth / 2;
        mRect.right = w - getPaddingRight() - mWidth / 2;
        mRect.bottom = h - getPaddingBottom() - mWidth / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float degree = 360 * mProgress / mMaxProgress;

        // 背景
        mPaint.setStrokeWidth(mBgWidth);
        mPaint.setColor(mBackground);
        canvas.drawArc(mRect, 0, 360, false, mPaint);

        // 进度
        mPaint.setStrokeWidth(mWidth);
        mPaint.setColor(mColor);
        //最小角度设为１，避免画布空白
        canvas.drawArc(mRect, -90, degree <= 0 ? 1 : degree, false, mPaint);
    }

    public void setProgressBarListener(RoundProgressBarListener listener) {
        mListener = listener;
    }

    public RoundProgressBarListener getProgressBarListener() {
        return mListener;
    }

    public float getMaxProgress() {
        return mMaxProgress;
    }

    public void setBgCircleColor(int background) {
        mBackground = background;
    }

    public void setBgCircleWidth(int bgWidth) {
        mBgWidth = bgWidth;
    }

    public void setCircleColor(int color) {
        mColor = color;
    }

    public void setCircleWidth(int width) {
        mWidth = width;
    }

    public int getBgCircleColor() {
        return mBackground;
    }

    public int getBgCircleWidth() {
        return mBgWidth;
    }

    public int getCircleColor() {
        return mColor;
    }

    public int getCirlceWidth() {
        return mWidth;
    }

    public interface RoundProgressBarListener {
        void onProgressChanged(float progress);
    }
}
