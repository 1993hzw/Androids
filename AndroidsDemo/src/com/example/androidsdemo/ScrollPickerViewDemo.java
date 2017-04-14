package com.example.androidsdemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.androidsdemo.view.SlotMachine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.forward.androids.utils.DateUtil;
import cn.forward.androids.utils.LogUtil;
import cn.forward.androids.views.BitmapScrollPicker;
import cn.forward.androids.views.ScrollPickerView;
import cn.forward.androids.views.StringScrollPicker;


public class ScrollPickerViewDemo extends Activity {

    // 初始月份
    private static final int ORIGIN_YEAR = 2000;
    private static final int ORIGIN_MONTH = 1;
    private static final int ORIGIN_DAY = 1;

    // 可选择的年份，从1900到现在
    private static final String[] YEARS;

    static {
        ArrayList<String> list = new ArrayList<String>();
        int curYear = DateUtil.getYear();
        for (int i = 1900; i <= curYear; i++) {
            list.add(i + "");
        }
        YEARS = list.toArray(new String[list.size()]);
    }

    // 月份
    private static final String[] MONTHS = {"1", "2", "3", "4", "5", "6", "7",
            "8", "9", "10", "11", "12"};

    private StringScrollPicker mYearView;
    private StringScrollPicker mMonthView;
    private StringScrollPicker mDayView;

    private ScrollPickerView mPicker01;
    private BitmapScrollPicker mPicker02;

    private Button mBtnPlay;
    boolean mIsPlaying = false;
    private SlotMachine mSlotMachine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrollpickerview);

        setTitle("ScrollPickerView");

        mYearView = (StringScrollPicker) this.findViewById(R.id.view_year);
        mMonthView = (StringScrollPicker) this.findViewById(R.id.view_month);
        mDayView = (StringScrollPicker) this.findViewById(R.id.view_day);


        mPicker01 = (ScrollPickerView) findViewById(R.id.picker_01);
        mPicker02 = (BitmapScrollPicker) findViewById(R.id.picker_02);

        // 不允许父元素拦截事件，设置后可以保证在ScrollView下正常滚动
        mYearView.setDisallowInterceptTouch(true);
        mMonthView.setDisallowInterceptTouch(true);
        mDayView.setDisallowInterceptTouch(true);
        mPicker01.setDisallowInterceptTouch(true);
        mPicker02.setDisallowInterceptTouch(true);

        mBtnPlay = (Button) findViewById(R.id.btn_play);

        init();

    }

    private void init() {
        // 设置数据
        mYearView.setData(new ArrayList<String>(Arrays.asList(YEARS)));
        mMonthView.setData(new ArrayList<String>(Arrays.asList(MONTHS)));
        mDayView.setData(DateUtil.getMonthDaysArray(ORIGIN_YEAR, ORIGIN_MONTH));

        // 设置初始值
        mYearView.setSelectedPosition(mYearView.getData().indexOf(
                "" + ORIGIN_YEAR));
        mMonthView.setSelectedPosition(ORIGIN_MONTH - 1);
        mDayView.setSelectedPosition(ORIGIN_DAY - 1);

        // 更改年份
        mYearView.setOnSelectedListener(new ScrollPickerView.OnSelectedListener() {
            @Override
            public void onSelected(ScrollPickerView view, int position) {
                LogUtil.i("hzw", "year " + mYearView.getSelectedItem());

                int month = Integer.parseInt(mMonthView.getSelectedItem());
                // ２月份,更新天数
                if (month == 2) {
                    changeMonthDays();
                }
            }
        });

        // 更改月份
        mMonthView.setOnSelectedListener(new ScrollPickerView.OnSelectedListener() {
            @Override
            public void onSelected(ScrollPickerView view, int position) {

                LogUtil.i("hzw", "month " + mMonthView.getSelectedItem());

                changeMonthDays();


            }
        });

        // 老虎机
        mSlotMachine = (SlotMachine) findViewById(R.id.slotmachine);

        final CopyOnWriteArrayList<Bitmap> bitmaps = new CopyOnWriteArrayList<Bitmap>();
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.slot_01));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.slot_02));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.slot_03));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.slot_04));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.slot_05));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.slot_06));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.slot_07));

        mSlotMachine.setData(bitmaps);
        mSlotMachine.setSlotMachineListener(new SlotMachine.SlotMachineListener() {
            @Override
            public void onFinish(int pos01, int pos02, int pos03) {
                mIsPlaying = false;
                Toast.makeText(getApplicationContext(), pos01 + "," + pos02 + "," + pos03, Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean acceptWinResult(int position) {
                return true;
            }
        });


        mBtnPlay.setOnClickListener(new View.OnClickListener() {
            Random mRandom = new Random();

            @Override
            public void onClick(View v) {
                if (mIsPlaying) {
                    return;
                }
                mIsPlaying = true;
                // 开始滚动，模拟50％的中奖概率
                if (mRandom.nextInt(2) != 5) { // 中奖
                    mSlotMachine.play(mRandom.nextInt(bitmaps.size()));
                } else { //
                    mSlotMachine.play(-1);
                }
            }
        });


        mPicker02.setData(bitmaps);
        mPicker02.setIsCirculation(false); // 设置非循环滚动
        mPicker02.setDrawMode(BitmapScrollPicker.DRAW_MODE_FULL);
    }

    // 更新天数
    private void changeMonthDays() {
        int year = Integer.parseInt(mYearView.getSelectedItem());
        int month = Integer.parseInt(mMonthView.getSelectedItem());
        int day = Integer.parseInt(mDayView.getSelectedItem());
        List<String> dayList = DateUtil.getMonthDaysArray(year, month);

        mDayView.setData(dayList);
        mDayView.setSelectedPosition(day > dayList.size() ? dayList
                .size() - 1 : day - 1);
    }

}
