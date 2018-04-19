package com.example.androidsdemo;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import cn.forward.androids.views.EasyAdapter;
import cn.forward.androids.views.ScrollPickerView;


public class EasyAdapterDemo extends Activity {

    private EasyAdapter<MySelectionHolder> easyAdapter;
    private RecyclerView recyclerView;
    private Spinner spinnerMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easyadpater);


        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(10, 10, 10, 10);
            }
        });

        recyclerView.setLayoutManager(layoutManager);
        final List<String> data = Arrays.asList("篮球", "足球", "羽毛球", "乒乓球", "排球", "橄榄球", "棒球");

        // 创建一个支持多选的适配器
        easyAdapter = new EasyAdapter<MySelectionHolder>(this, EasyAdapter.Mode.MULTI_SELECT, 0) {
            @Override
            public MySelectionHolder whenCreateViewHolder(ViewGroup parent, int viewType) {
                return new MySelectionHolder(View.inflate(EasyAdapterDemo.this, R.layout.item_string, null));
            }

            @Override
            public void whenBindViewHolder(MySelectionHolder holder, int position) {
                holder.textView.setText(data.get(position));
            }

            @Override
            public int getItemCount() {
                return data.size();
            }
        };

        // 设置点击监听器
        easyAdapter.setOnItemClickedListener(new EasyAdapter.OnItemClickedListener() {
            @Override
            public void onClicked(int position) {
                Toast.makeText(EasyAdapterDemo.this, "clicked:" + position, Toast.LENGTH_SHORT).show();
            }
        });

        easyAdapter.setOnItemLongClickedListener(new EasyAdapter.OnItemLongClickedListener() {
            @Override
            public boolean onLongClicked(int position) {
                Toast.makeText(EasyAdapterDemo.this, "long clicked:" + position, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        // 设置单选监听器
        easyAdapter.setOnSingleSelectListener(new EasyAdapter.OnSingleSelectListener() {
            @Override
            public void onSelected(int position) {
                Toast.makeText(EasyAdapterDemo.this, "selected:" + position, Toast.LENGTH_SHORT).show();
            }
        });

        // 设置多选监听器
        easyAdapter.setOnMultiSelectListener(new EasyAdapter.OnMultiSelectListener() {
            @Override
            public void onSelected(int position, boolean isSelected) {
                Toast.makeText(EasyAdapterDemo.this, "selected:" + position + " " + isSelected, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onSelected(EasyAdapter.SelectionMode selectionMode, Set<Integer> selectedSet) {
                switch (selectionMode) {
                    case SELECT_ALL:
                        Toast.makeText(EasyAdapterDemo.this, "select all:" + selectedSet, Toast.LENGTH_SHORT).show();
                        break;

                    case UNSELECT_ALL:
                        Toast.makeText(EasyAdapterDemo.this, "unselect all:" + selectedSet, Toast.LENGTH_SHORT).show();
                        break;

                    case REVERSE_SELECTED:
                        Toast.makeText(EasyAdapterDemo.this, "reverse selected:" + selectedSet, Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onOutOfMax(int position) {
                Toast.makeText(EasyAdapterDemo.this,
                        "select:" + position + " onOutOfMax:" + easyAdapter.getMaxSelectionCount(), Toast.LENGTH_SHORT).
                        show();

            }
        });

        recyclerView.setAdapter(easyAdapter);


        ArrayList<String> dataList = new ArrayList<String>();
        dataList.add("点击模式");
        dataList.add("单选模式");
        dataList.add("多选模式");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dataList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMode = (Spinner) findViewById(R.id.spinnerMode);
        spinnerMode.setAdapter(spinnerAdapter);
        spinnerMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                findViewById(R.id.panelMultiSel).setVisibility(View.GONE);
                switch (position) { // 设置模式
                    case 0:
                        easyAdapter.setMode(EasyAdapter.Mode.CLICK);
                        break;
                    case 1:
                        easyAdapter.setMode(EasyAdapter.Mode.SINGLE_SELECT);
                        break;
                    case 2:
                        easyAdapter.setMode(EasyAdapter.Mode.MULTI_SELECT);
                        findViewById(R.id.panelMultiSel).setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerMode.setSelection(2);

        ScrollPickerView pickerMaxSelect = (ScrollPickerView) findViewById(R.id.pickerMaxSelect);
        // 设置最大可选数量
        pickerMaxSelect.setOnSelectedListener(new ScrollPickerView.OnSelectedListener() {
            @Override
            public void onSelected(ScrollPickerView scrollPickerView, int position) {
                easyAdapter.setMaxSelectionCount(position);
            }
        });
        pickerMaxSelect.setData(Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7"));
        pickerMaxSelect.setSelectedPosition(0);

    }

    public void selectAll(View view) {
        easyAdapter.selectAll();
    }

    public void reverseSelectAll(View view) {
        easyAdapter.reverseSelected();
    }
}

class MySelectionHolder extends RecyclerView.ViewHolder {
    TextView textView;

    public MySelectionHolder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.textview);
    }
}
