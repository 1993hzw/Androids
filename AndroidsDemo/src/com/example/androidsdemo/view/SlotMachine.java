package com.example.androidsdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import com.example.androidsdemo.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.forward.androids.utils.ThreadUtil;
import cn.forward.androids.views.BitmapScrollPicker;
import cn.forward.androids.views.ScrollPickerView;

/**
 * 老虎机
 * Created by huangziwei on 16-12-5.
 */
public class SlotMachine extends FrameLayout implements ScrollPickerView.OnSelectedListener {

    // 滚动的时间
    private static int DURATION01 = 3000;
    private static int DURATION02 = 3500;
    private static int DURATION03 = 4000;

    /**
     * 重置滚动的时间
     *
     * @param duration01
     * @param duration02
     * @param duration03
     */
    public static void resetDuration(int duration01, int duration02, int duration03) {
        DURATION01 = duration01;
        DURATION02 = duration02;
        DURATION03 = duration03;
        mDurationList.clear();
        mDurationList.addAll(Arrays.asList(DURATION01, DURATION02, DURATION03));
    }

    private final static ArrayList<Integer> mDurationList = new ArrayList<Integer>(Arrays.asList(DURATION01, DURATION02, DURATION03));
    private Random mRandom = new Random();

    private BitmapScrollPicker mSlot01, mSlot02, mSlot03;
    private Context mContext;
    private boolean mIsPlaying;
    private int mFinishedCounter = 0;
    private int[] mSelectedArray;
    private CopyOnWriteArrayList<Bitmap> mPrizeList;
    private SlotMachineListener mSlotMachineListener;


    public SlotMachine(Context context) {
        this(context, null);
    }

    public SlotMachine(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        View view = inflate(mContext, R.layout.slot_machine_view, this);
        mSlot01 = (BitmapScrollPicker) view.findViewById(R.id.slot_view_01);
        mSlot02 = (BitmapScrollPicker) view.findViewById(R.id.slot_view_02);
        mSlot03 = (BitmapScrollPicker) view.findViewById(R.id.slot_view_03);
        mSlot01.setOnSelectedListener(this);
        mSlot02.setOnSelectedListener(this);
        mSlot03.setOnSelectedListener(this);
        mSlot01.setDisallowTouch(true);
        mSlot02.setDisallowTouch(true);
        mSlot03.setDisallowTouch(true);
        mSlot01.setVisibleItemCount(3);
        mSlot02.setVisibleItemCount(3);
        mSlot03.setVisibleItemCount(3);
        mSlot01.setDrawMode(BitmapScrollPicker.DRAW_MODE_SPECIFIED_SIZE);
        mSlot02.setDrawMode(BitmapScrollPicker.DRAW_MODE_SPECIFIED_SIZE);
        mSlot03.setDrawMode(BitmapScrollPicker.DRAW_MODE_SPECIFIED_SIZE);
        mSelectedArray = new int[3];

        int size = dip2px(68);
        mSlot01.setDrawModeSpecifiedSize(size, size);
        mSlot02.setDrawModeSpecifiedSize(size, size);
        mSlot03.setDrawModeSpecifiedSize(size, size);

        setClickable(true);

    }

    public void setData(CopyOnWriteArrayList<Bitmap> data) {
        mPrizeList = data;
        mSlot01.setData(mPrizeList);
        mSlot02.setData(mPrizeList);
        mSlot03.setData(mPrizeList);
        mSlot01.setSelectedPosition(0);
        mSlot02.setSelectedPosition(0);
        mSlot03.setSelectedPosition(0);

        mSlot01.setSelectedPosition(0);
        mSlot02.setSelectedPosition(0);
        mSlot03.setSelectedPosition(0);
    }

    public CopyOnWriteArrayList<Bitmap> getData() {
        return mPrizeList;
    }

    public void setSlotMachineListener(SlotMachineListener slotMachineListener) {
        mSlotMachineListener = slotMachineListener;
    }

    public SlotMachineListener getSlotMachineListener() {
        return mSlotMachineListener;
    }

    @Override
    public void onSelected(ScrollPickerView slotView, int position) {
        if (mIsPlaying) {
            mFinishedCounter++;
            if (slotView == mSlot01) {
                mSelectedArray[0] = position;
            } else if (slotView == mSlot02) {
                mSelectedArray[1] = position;
            } else if (slotView == mSlot03) {
                mSelectedArray[2] = position;
            }
            if (mFinishedCounter >= 3) {

                mFinishedCounter = 0;
                if (mSlotMachineListener != null) {
                    boolean win = false;
                    boolean makeup = false;
                    if (mSelectedArray[0] == mSelectedArray[1] && mSelectedArray[0] == mSelectedArray[2]) { // win
                        // 是否取消中奖，采用弥补动画，使之变成不中奖的结果
                        win = mSlotMachineListener.acceptWinResult(mSelectedArray[0]);
                        makeup = !win;
                    }
                    final boolean finalMakeup = makeup;
                    Runnable task = new Runnable() {
                        public void run() {
                            if (finalMakeup) {
                                mSelectedArray[2] = mSlot03.getSelectedPosition();
                            }
                            mSlotMachineListener.onFinish(mSelectedArray[0], mSelectedArray[1], mSelectedArray[2]);
                            mIsPlaying = false;
                        }
                    };

                    if (makeup) {
                        makeUpPurchaseFailed(mSelectedArray[2]);
                        // 等待弥补动画结束
                        ThreadUtil.getInstance().runOnMainThread(task, 1200);
                    } else {
                        task.run();
                    }
                }
            }
        }
    }


    /**
     * 开始滚动，
     *
     * @param prizePosition 奖品的索引，如果prizePosition＜０或者　prizePosition＞＝总的奖品数，则表示不中奖
     */
    public boolean play(int prizePosition) {
        if (!isClickable() || mIsPlaying) {
            return false;
        }
        mFinishedCounter = 0;
        mIsPlaying = true;

        int slot01, slot02, slot03;
        int duration01, duration02, duration03;

//        Collections.shuffle(mDurationList);
        duration01 = mDurationList.get(0);
        duration02 = mDurationList.get(1);
        duration03 = mDurationList.get(2);

        if (prizePosition < 0 || prizePosition >= mPrizeList.size()) { // 不中奖,控制三个中有两个相同
            // pos01表示时间最短的停留位置，pos03表示时间最长
            int pos01, pos02, pos03;
            pos01 = mRandom.nextInt(mPrizeList.size());
            if (mRandom.nextInt(3) == 0) { // ０１，０２相同的概率为1／３
                pos02 = pos01;
                pos03 = mRandom.nextInt(mPrizeList.size());
            } else {
                pos02 = mRandom.nextInt(mPrizeList.size());
                if (mRandom.nextInt(4) == 0) { // ０１，０3相同的概率为１／4
                    pos03 = pos01;
                } else {
                    pos03 = mRandom.nextInt(mPrizeList.size());
                }
            }
            if (pos01 == pos02 && pos01 == pos03) {
                pos01 = (pos01 + 1) % mPrizeList.size();
            }

            // 按照时间排序老虎机的窗口，如［1，3，2］表示slot01的时间最短，接着是slot03,slot02
            HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
            map.put(mDurationList.indexOf(DURATION01) + 1, pos01); // 时间最短的那个slot停留的位置
            map.put(mDurationList.indexOf(DURATION02) + 1, pos02);
            map.put(mDurationList.indexOf(DURATION03) + 1, pos03);

            slot01 = map.get(1);
            slot02 = map.get(2);
            slot03 = map.get(3);
        } else {
            slot01 = slot02 = slot03 = prizePosition;
        }

        mSlot01.autoScrollFast(slot01, duration01);
        mSlot02.autoScrollFast(slot02, duration02);
        mSlot03.autoScrollFast(slot03, duration03);
        return true;
    }

    /**
     * 弥补购买失败，使其中一个往下滚动一列，造成未抽中的假象
     *
     * @param prizePosition
     */
    public void makeUpPurchaseFailed(int prizePosition) {
        int moveY = mSlot03.getItemHeight();
        mSlot03.autoScrollTo(moveY, 1200, new LinearInterpolator(), false);
    }

    /**
     * 设置奖品图片大小
     *
     * @param width
     * @param height
     */
    public void setDrawModeSpecifiedSize(int width, int height) {
        mSlot01.setDrawModeSpecifiedSize(width, height);
        mSlot02.setDrawModeSpecifiedSize(width, height);
        mSlot03.setDrawModeSpecifiedSize(width, height);
    }

    public interface SlotMachineListener {

        /**
         * 滚动结束时回调
         * @param pos01
         * @param pos02
         * @param pos03
         */
        void onFinish(int pos01, int pos02, int pos03);

        /**
         * 是否接受该次中奖结果
         * @param position
         * @return 返回true则表示确认该次赢得奖品，false则表示取消该次奖品
         */
        boolean acceptWinResult(int position);
    }

    public boolean isPlaying() {
        return mIsPlaying;
    }

    public boolean canPlay() {
        return isClickable() && !mIsPlaying;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        mSlot01.invalidate();
        mSlot02.invalidate();
        mSlot03.invalidate();
    }

    public int dip2px(float dipVlue) {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        float sDensity = metrics.density;
        return (int) (dipVlue * sDensity + 0.5F);
    }

}
