package com.example.androidsdemo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import cn.forward.androids.utils.LogUtil;

/**
 * @author huangziwei
 */
public class MainActivity extends ListActivity {

    public static final String TITLE = "title";
    public static final String SUBTITLE = "subtitle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListAdapter(new SimpleAdapter(this, createData(),
                android.R.layout.simple_list_item_2, new String[]{TITLE,
                SUBTITLE}, new int[]{android.R.id.text1,
                android.R.id.text2}));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (position == 0) {
            startActivity(new Intent(getApplicationContext(),
                    ShapeImageViewDemo.class));
        } else if (position == 1) {
            startActivity(new Intent(getApplicationContext(),
                    MaskImageViewDemo.class));
        } else if (position == 2) {
            startActivity(new Intent(getApplicationContext(),
                    RatioImageViewDemo.class));
        } else if (position == 3) {
            startActivity(new Intent(getApplicationContext(),
                    ScrollPickerViewDemo.class));
        } else if (position == 4) {
            startActivity(new Intent(getApplicationContext(),
                    AnimatorUtilDemo.class));
        } else if (position == 5) {
            startActivity(new Intent(getApplicationContext(),
                    KeyboardLayoutDemo.class));
        } else if (position == 6) {
            startActivity(new Intent(getApplicationContext(),
                    DragListViewDemo.class));
        } else if (position == 7) {
            startActivity(new Intent(getApplicationContext(),
                    OtherDemo.class));
        }
    }

    private List<Map<String, String>> createData() {
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        data.add(createItem("ShapeImageView", "可设置形状(圆形、圆角矩形)的ImageView，抗锯齿"));
        data.add(createItem("MaskImageView", "可在背景图和前景图显示遮罩效果的ImageView"));
        data.add(createItem("RatioImageView", "可以设置宽高比例的ImageView"));
        data.add(createItem("ScrollPickerView", "滚动选择器，可设置是否循环滚动，可见条目数"));
        data.add(createItem("AnimatorUtil", "对AnimatorSet进行封装，便以链式构建动画"));
        data.add(createItem("KeyboardLayout", "监听输入法键盘的弹起与隐藏"));
        data.add(createItem("DragListView", "可拖拽的ListView"));
        data.add(createItem("其他", ""));

        return data;
    }

    private Map<String, String> createItem(String title, String subtitle) {
        Map<String, String> item = new HashMap<String, String>();

        item.put(TITLE, title);
        item.put(SUBTITLE, subtitle);

        return item;
    }
}
