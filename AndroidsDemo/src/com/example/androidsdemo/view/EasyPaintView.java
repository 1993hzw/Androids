package com.example.androidsdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created on 24/06/2018.
 */

public class EasyPaintView extends View {

    private Paint mPaint = new Paint();
    private Path mPath = new Path();

    {
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(20);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public EasyPaintView(Context context) {
        super(context);
    }

    public EasyPaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EasyPaintView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private boolean mIsFirstPoint = true;
    private float mLastX, mLastY;

    public void reset() {
        mIsFirstPoint = true;
    }

    public void addPoint(float x, float y) {
        if (mIsFirstPoint) {
            mIsFirstPoint = false;
            mLastX = x;
            mLastY = y;
            mPath.moveTo(x, y);
        } else {
            mPath.quadTo(
                    mLastX,
                    mLastY,
                    (x + mLastX) / 2,
                    (y + mLastY) / 2);
            mLastX = x;
            mLastY = y;
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(mPath, mPaint);
    }
}
