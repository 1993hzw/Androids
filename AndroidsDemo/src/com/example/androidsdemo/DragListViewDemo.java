package com.example.androidsdemo;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import com.example.androidsdemo.adapter.PluginListAdapter;

import java.util.ArrayList;

import cn.forward.androids.utils.ViewUtil;
import cn.forward.androids.views.DragListView;

/**
 * 可拖拽的ListView
 * Created by huangziwei on 16-6-27.
 */
public class DragListViewDemo extends Activity {

    private DragListView mListView;
    private PluginListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_listview);
        init();

        mListView.setDragItemListener(new DragListView.SimpleAnimationDragItemListener() {

            private Rect mFrame = new Rect();
            private boolean mIsSelected;

            @Override
            public boolean canDrag(View dragView, int x, int y) {
                // 获取可拖拽的图标
                View dragger = dragView.findViewById(R.id.dl_plugin_move);
                if (dragger == null || dragger.getVisibility() != View.VISIBLE) {
                    return false;
                }
                float tx = x - ViewUtil.getX(dragView);
                float ty = y - ViewUtil.getY(dragView);
                dragger.getHitRect(mFrame);
                if (mFrame.contains((int) tx, (int) ty)) { // 当点击拖拽图标才可进行拖拽
                    return true;
                }
                return false;
            }


            @Override
            public void beforeDrawingCache(View dragView) {
                mIsSelected = dragView.isSelected();
                View drag = dragView.findViewById(R.id.dl_plugin_move);
                dragView.setSelected(true);
                if (drag != null) {
                    drag.setSelected(true);
                }
            }

            @Override
            public void afterDrawingCache(View dragView) {
                dragView.setSelected(mIsSelected);
                View drag = dragView.findViewById(R.id.dl_plugin_move);
                if (drag != null) {
                    drag.setSelected(false);
                }
            }

            @Override
            public boolean canExchange(int srcPosition, int position) {
                boolean result = mAdapter.exchange(srcPosition, position);
                return result;
            }
        });

        // 模拟数据
        ArrayList<PluginItem> addedItem = new ArrayList<PluginItem>();
        ArrayList<PluginItem> notAddedItem = new ArrayList<PluginItem>();

        for (int i = 0; i < 6; i++) {
            addedItem.add(new PluginItem("item:" + i));
        }
        for (int i = 10; i < 20; i++) {
            notAddedItem.add(new PluginItem("item:" + i));
        }

        mAdapter = new PluginListAdapter(this, addedItem, notAddedItem, new OnStateBtnClickListener());
        mListView.setAdapter(mAdapter);
    }

    private void init() {
        mListView = (DragListView) findViewById(R.id.draglist);
    }

    public static class PluginItem {

        public String mName;

        public PluginItem() {

        }

        public PluginItem(String name) {
            mName = name;
        }


    }

    /**
     * 点击已添加／未添加按钮
     */
    public class OnStateBtnClickListener implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
            PluginItem item = (PluginItem) v.getTag();
            if (item == null) {
                return;
            }
            if (mAdapter.isAdded(item)) { // 已添加
                mAdapter.removeItem(item);
            } else {
                mAdapter.addItem(item);
            }
        }
    }
}
