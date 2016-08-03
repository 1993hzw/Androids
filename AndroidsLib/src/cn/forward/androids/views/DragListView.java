package cn.forward.androids.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * item可拖拽的ListView
 * Created by huangziwei on 16-6-29.
 */
public class DragListView extends ListView {

    private int mLastPosition; // 上次的位置，用于判断是否跟当前交换位置
    private int mCurrentPosition; // 手指点击准备拖动的时候,当前拖动项在列表中的位置.

    private int mAutoScrollUpY; // 拖动的时候，开始向上滚动的边界
    private int mAutoScrollDownY; // 拖动的时候，开始向下滚动的边界

    private int mLastY;

    private int mDragViewOffset; // 触摸点在itemView中的高度

    private DragItemListener mDragItemListener;

    private boolean mHasStart = false;

    private Bitmap mBitmap; // 拖拽的itemView图像

    private View mItemView;

    private boolean mIsHideItemView = true; // 拖拽时是否隐藏itemView

    public DragListView(Context context) {
        this(context, null);
    }

    public DragListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 触摸事件处理
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
                if (mBitmap != null) {
                    stopDrag();
                    invalidate();
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mBitmap != null) {
                    if (!mHasStart) {
                        mDragItemListener.startDrag(mCurrentPosition);
                        mHasStart = true;
                    }
                    int moveY = (int) ev.getY();
                    if (moveY < 0) { // 限制触摸范围在ＬｉｓｔＶｉｅｗ中
                        moveY = 0;
                    } else if (moveY > getHeight()) {
                        moveY = getHeight();
                    }
                    onMove(moveY);
                    mLastY = moveY;
                    invalidate();
                    return true;
                }
                break;
            case MotionEvent.ACTION_DOWN: // 判断是否进拖拽
                stopDrag();
                int x = (int) ev.getX(); // 获取相对与ListView的x坐标
                int y = (int) ev.getY(); // 获取相应与ListView的y坐标
                int temp = pointToPosition(x, y);
                if (temp == AdapterView.INVALID_POSITION) { // 无效不进行处理
                    return super.dispatchTouchEvent(ev);
                }
                mLastPosition = mCurrentPosition = temp;

                // 获取当前位置的视图(可见状态)
                ViewGroup itemView = (ViewGroup) getChildAt(mCurrentPosition - getFirstVisiblePosition());

                if (itemView != null && mDragItemListener != null && mDragItemListener.canDrag(itemView, x, y)) {

                    // 触摸点在item项中的高度
                    mDragViewOffset = y - itemView.getTop();

                    mDragItemListener.beforeDrawingCache(itemView);
                    itemView.setDrawingCacheEnabled(true); // 开启cache.
                    mBitmap = Bitmap.createBitmap(itemView.getDrawingCache()); // 根据cache创建一个新的bitmap对象.
                    itemView.setDrawingCacheEnabled(false);
                    mDragItemListener.afterDrawingCache(itemView);
                    mHasStart = false;
                    mLastY = y;

                    if (mIsHideItemView) { // 隐藏itemView
                        hideItemView();
                    }
                    invalidate();
                    return true;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        // 绘制拖拽的itemView
        if (mBitmap != null && !mBitmap.isRecycled()) {
            canvas.drawBitmap(mBitmap, 0, mLastY - mDragViewOffset, null);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mAutoScrollUpY = w / 4; // 取得向上滚动的边际，大概为该控件的1/3
        mAutoScrollDownY = h * 3 / 4; // 取得向下滚动的边际，大概为该控件的2/3
    }


    /**
     * 拖动执行，在Move方法中执行
     *
     * @param y
     */
    public void onMove(int y) {
        // 为了避免滑动到分割线的时候，返回-1的问题
        int tempPosition = pointToPosition(0, y);
        if (tempPosition != INVALID_POSITION) {
            mCurrentPosition = tempPosition;
        }
        if (y < getChildAt(0).getTop()) { // 超出边界处理(如果向上超过第二项Top的话，那么就放置在第一个位置)
            mCurrentPosition = 0;
        } else if (y > getChildAt(getChildCount() - 1).getBottom()) { // // 如果拖动超过最后一项的最下边那么就防止在最下边
            mCurrentPosition = getAdapter().getCount() - 1;
        }
        checkExchange(y); // 时时交换
        checkScroller(y); // listview移动.
    }

    /***
     * ListView的移动.
     * 要明白移动原理：当我移动到下端的时候，ListView向上滑动，当我移动到上端的时候，ListView要向下滑动。正好和实际的相反.
     */

    public void checkScroller(int y) {
        int offset = 0;
        if (y < mAutoScrollUpY) { // ListView需要下滑
            if (y < mLastY) {
                offset = (mAutoScrollUpY - y) / 5; // 时时步伐
            }
        } else if (y > mAutoScrollDownY) { // ListView需要上滑
            if (y > mLastY) {
                offset = (mAutoScrollDownY - y) / 5; // 时时步伐
            }
        }

        if (offset != 0) {
            View view = getChildAt(mCurrentPosition - getFirstVisiblePosition());
            if (view != null) {
                setSelectionFromTop(mCurrentPosition, view.getTop() + offset);
            }
        }

    }

    /**
     * 停止拖动，删除影像
     */
    public void stopDrag() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
            if (mDragItemListener != null) {
                mDragItemListener.onRelease(mCurrentPosition);
            }
        }
        if (mItemView != null) {
            mItemView.setVisibility(View.VISIBLE);
            mItemView = null;
        }
    }

    /***
     * 拖动时时change
     */
    private void checkExchange(int y) {
// 数据交换
        if (mCurrentPosition != mLastPosition) {
            if (mDragItemListener != null) {
                if (mDragItemListener.onExchange(mLastPosition, mCurrentPosition)) { // 进行数据交换,true则表示交换成功
                    mLastPosition = mCurrentPosition;

                    if (mIsHideItemView) { // 隐藏实际的itemView
                        hideItemView();
                    }
                }
            }
        }
    }

    // 隐藏实际的itemView
    private void hideItemView() {
        if (mItemView != null) {
            mItemView.setVisibility(View.VISIBLE);
        }
        mItemView = getChildAt(mCurrentPosition - getFirstVisiblePosition()); // 隐藏实际的itemView
        if (mItemView != null) {
            mItemView.setVisibility(View.INVISIBLE);
        }
    }

    public void setDragItemListener(DragItemListener listener) {
        mDragItemListener = listener;
    }

    public DragItemListener getDragListener() {
        return mDragItemListener;
    }

    /**
     * 　拖拽监听器
     */
    public interface DragItemListener {

        /**
         * 数据交换
         *
         * @param srcPosition
         * @param position
         * @return 返回true，则确认数据交换;返回false则表示放弃
         */
        boolean onExchange(int srcPosition, int position);

        /**
         * 释放手指
         *
         * @param position
         */
        void onRelease(int position);

        /**
         * 是否可以拖拽
         *
         * @param itemView
         * @param x        当前触摸的坐标
         * @param y
         * @return
         */
        boolean canDrag(View itemView, int x, int y);

        /**
         * 开始拖拽
         *
         * @param position
         */
        void startDrag(int position);

        /**
         * 在生成拖影（itemView.getDrawingCache()）之前
         *
         * @param itemView
         */
        void beforeDrawingCache(View itemView);

        /**
         * 在生成拖影（itemView.getDrawingCache()）之后
         *
         * @param itemView
         */
        void afterDrawingCache(View itemView);
    }
}
