package com.example.androidsdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.androidsdemo.view.EasyPaintView;

import cn.forward.androids.ScaleGestureDetectorApi27;
import cn.forward.androids.TouchGestureDetector;

/**
 * Created on 23/06/2018.
 */

public class TouchGestureDetectorDemo extends Activity {

    private static final String TAG = "TEST";
    private View mTargetView;
    private EasyPaintView mEasyPaintView;
    private TouchGestureDetector mTouchGestureDetector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touchgesturedetector);

        mTargetView = findViewById(R.id.image_View);
        mEasyPaintView = (EasyPaintView) findViewById(R.id.easy_paint_view);


        mTouchGestureDetector = new TouchGestureDetector(this, new TouchGestureDetector.OnTouchGestureListener() {
            private Float mLastFocusX;
            private Float mLastFocusY;
            // 手势操作相关
            private float mTouchCentreX, mTouchCentreY;

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.d(TAG, "onSingleTapConfirmed: ");
                Toast.makeText(TouchGestureDetectorDemo.this, "onSingleTapConfirmed", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.d(TAG, "onDoubleTap: ");
                Toast.makeText(TouchGestureDetectorDemo.this, "onDoubleTap", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                Log.d(TAG, "onDown: ");
                return true;
            }

            @Override
            public void onUpOrCancel(MotionEvent e) {
                Log.d(TAG, "onUpOrCancel: ");
            }

            @Override
            public void onScrollBegin(MotionEvent e) {
                Log.d(TAG, "onScrollBegin: ");
                mEasyPaintView.addPoint(e.getX(), e.getY());
            }

            @Override
            public void onScrollEnd(MotionEvent e) {
                Log.d(TAG, "onScrollEnd: ");
                mEasyPaintView.reset();
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.d(TAG, "onScroll: " + e2.getX() + " " + e2.getY());
                mEasyPaintView.addPoint(e2.getX(), e2.getY() - mEasyPaintView.getTop());
                return true;
            }


            @Override
            public boolean onScaleBegin(ScaleGestureDetectorApi27 detector) {
                Log.d(TAG, "onScaleBegin: ");
                mLastFocusX = null;
                mLastFocusY = null;
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetectorApi27 detector) {
                Log.d(TAG, "onScaleEnd: ");
                float scale = 1f;
                mTargetView.setScaleX(scale);
                mTargetView.setScaleY(scale);
                mTargetView.setTranslationX(0);
                mTargetView.setTranslationY(0);
            }

            @Override
            public boolean onScale(ScaleGestureDetectorApi27 detector) {
                Log.d(TAG, "onScale: ");
                // 屏幕上的焦点
                mTouchCentreX = detector.getFocusX();
                mTouchCentreY = detector.getFocusY();

                if (mLastFocusX != null && mLastFocusY != null) { // 焦点改变
                    final float dx = mTouchCentreX - mLastFocusX;
                    final float dy = mTouchCentreY - mLastFocusY;
                    // 移动图片

                    mTargetView.setTranslationX(mTargetView.getTranslationX() + dx);
                    mTargetView.setTranslationY(mTargetView.getTranslationY() + dy);
                }

                // 缩放图片
                float scale = mTargetView.getScaleX() * detector.getScaleFactor();
                mTargetView.setScaleX(scale);
                mTargetView.setScaleY(scale);

                mLastFocusX = mTouchCentreX;
                mLastFocusY = mTouchCentreY;

                return true;
            }
        });

        // 结合绘画场景设置属性
        mTouchGestureDetector.setIsLongpressEnabled(false); // 绘画应该取消长按

        // 下面两行绘画场景下应该设置间距为大于等于1，否则设为0双指缩放后抬起其中一个手指仍然可以移动
        mTouchGestureDetector.setScaleSpanSlop(1);
        mTouchGestureDetector.setScaleMinSpan(1); // 最小缩放距离，即双指大于这个值时才可以缩放

        mTouchGestureDetector.setIsScrollAfterScaled(false);

        mEasyPaintView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mTouchGestureDetector.onTouchEvent(event);
            }
        });
        mEasyPaintView.setScaleX(0.5f);
        mEasyPaintView.setScaleY(0.5f);
    }

}
