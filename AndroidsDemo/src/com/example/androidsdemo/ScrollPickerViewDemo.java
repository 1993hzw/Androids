package com.example.androidsdemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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

    // 模拟老虎机
    private BitmapScrollPicker mBitmapPicker01;
    private BitmapScrollPicker mBitmapPicker02;
    private BitmapScrollPicker mBitmapPicker03;

    private ScrollPickerView mPicker01;
    private ScrollPickerView mPicker02;

    private Button mBtnPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrollpickerview);

        setTitle("ScrollPickerView");

        mYearView = (StringScrollPicker) this.findViewById(R.id.view_year);
        mMonthView = (StringScrollPicker) this.findViewById(R.id.view_month);
        mDayView = (StringScrollPicker) this.findViewById(R.id.view_day);

        mBitmapPicker01 = (BitmapScrollPicker) this.findViewById(R.id.bitmap_picker01);
        mBitmapPicker02 = (BitmapScrollPicker) this.findViewById(R.id.bitmap_picker02);
        mBitmapPicker03 = (BitmapScrollPicker) this.findViewById(R.id.bitmap_picker03);

        mPicker01 = (ScrollPickerView) findViewById(R.id.picker_01);
        mPicker02 = (ScrollPickerView) findViewById(R.id.picker_02);

        // 不允许父元素拦截事件
        mYearView.setDisallowInterceptTouch(true);
        mMonthView.setDisallowInterceptTouch(true);
        mDayView.setDisallowInterceptTouch(true);
        mBitmapPicker01.setDisallowInterceptTouch(true);
        mBitmapPicker02.setDisallowInterceptTouch(true);
        mBitmapPicker03.setDisallowInterceptTouch(true);
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


        final ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.slot_01));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.slot_02));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.slot_03));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.slot_04));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.slot_05));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.slot_06));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.slot_07));
        mBitmapPicker01.setData(bitmaps);
        mBitmapPicker02.setData(bitmaps);
        mBitmapPicker03.setData(bitmaps);
        mBitmapPicker01.setDisallowTouch(true); // 不允许触摸
        mBitmapPicker02.setDisallowTouch(true); // 不允许触摸
        mBitmapPicker03.setDisallowTouch(true); // 不允许触摸

        mPicker02.setData(bitmaps);
        mPicker02.setIsCirculation(false); // 设置非循环滚动

        ScrollPickerView.OnSelectedListener listener = new ScrollPickerView.OnSelectedListener() {
            int[] selectedList = new int[3];
            int counter = 0;

            @Override
            public void onSelected(ScrollPickerView scrollPickerView, int position) {
                if (scrollPickerView == mBitmapPicker01) {
                    selectedList[0] = position;
                } else if (scrollPickerView == mBitmapPicker02) {
                    selectedList[1] = position;
                } else if (scrollPickerView == mBitmapPicker03) {
                    selectedList[2] = position;
                }
                counter++;
                if (counter >= 3) { // 当老虎机中三个都滚动完毕则提示结果
                    counter = 0;
                    Toast.makeText(getApplicationContext(), Arrays.toString(selectedList), Toast.LENGTH_SHORT).show();
                }
            }
        };

        mBitmapPicker01.setOnSelectedListener(listener);
        mBitmapPicker02.setOnSelectedListener(listener);
        mBitmapPicker03.setOnSelectedListener(listener);

        mBtnPlay.setOnClickListener(new View.OnClickListener() {
            Random mRandom = new Random();

            @Override
            public void onClick(View v) {
                mBitmapPicker01.autoScroll(mRandom.nextInt(bitmaps.size()), 5000);
                mBitmapPicker02.autoScroll(mRandom.nextInt(bitmaps.size()), 5500);
                mBitmapPicker03.autoScroll(mRandom.nextInt(bitmaps.size()), 6000);
            }
        });
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
