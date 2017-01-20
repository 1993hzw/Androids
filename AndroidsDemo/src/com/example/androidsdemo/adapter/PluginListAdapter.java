package com.example.androidsdemo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidsdemo.DragListViewDemo;
import com.example.androidsdemo.DragListViewDemo.PluginItem;
import com.example.androidsdemo.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by huangziwei on 16-6-29.
 */
public class PluginListAdapter extends BaseAdapter {

    public static final int TYPE_TAG_ADDED = 0;
    public static final int TYPE_TAG_NOT_ADDED = 1;
    public static final int TYPE_PLUGIN_ADDED = 2;
    public static final int TYPE_PLUGIN_NOT_ADDED = 3;


    private Context mContext;
    private ArrayList<PluginItem> mAddedPlugins; // 已添加
    private ArrayList<PluginItem> mNotAddedPlugins; // 未添加
    private DragListViewDemo.OnStateBtnClickListener mListener;


    public PluginListAdapter(Context context, ArrayList<PluginItem> added, ArrayList<PluginItem> notAdded, DragListViewDemo.OnStateBtnClickListener listener) {
        mContext = context;
        mAddedPlugins = added;
        mNotAddedPlugins = notAdded;
        mListener = listener;

    }

    private PluginItem getPlugin(int position) {
        if (position == 0) { // 跳过条目名称（已添加）
            return null;
        } else if (position < mAddedPlugins.size() + 1) {
            return mAddedPlugins.get(position - 1);
        } else if (position == mAddedPlugins.size() + 1) { // 跳过条目名称（未添加）
            return null;
        } else {
            return mNotAddedPlugins.get(position - mAddedPlugins.size() - 2);
        }
    }

    public boolean isAdded(PluginItem item) {
        return mAddedPlugins.contains(item);
    }

    /**
     * 交换数据的位置
     * @param src
     * @param dst
     * @return
     */
    public boolean exchange(int src, int dst) {
        boolean success = false;
        PluginItem srcItem = getPlugin(src);
        PluginItem dstItem = getPlugin(dst);
        int srcIndex = mAddedPlugins.indexOf(srcItem);
        int dstIndex = mAddedPlugins.indexOf(dstItem);
        if (srcIndex != -1 && dstIndex != -1) {
            Collections.swap(mAddedPlugins, srcIndex, dstIndex);
            success = true;
        }
        // 处理处理的数据在两个不同的集合内的情况
        if (!success) {
            if (srcIndex == -1 && dstIndex != -1) {
                srcIndex = mNotAddedPlugins.indexOf(srcItem);
                if (srcIndex != -1 && dstIndex != -1) {
                    mNotAddedPlugins.remove(srcIndex);
                    mNotAddedPlugins.add(srcIndex, dstItem);

                    mAddedPlugins.remove(dstIndex);
                    mAddedPlugins.add(dstIndex, srcItem);
                    success = true;
                }
            } else if (srcIndex != -1 && dstIndex == -1) {
                dstIndex = mNotAddedPlugins.indexOf(dstItem);
                if (srcIndex != -1 && dstIndex != -1) {
                    mNotAddedPlugins.remove(dstIndex);
                    mNotAddedPlugins.add(dstIndex, srcItem);

                    mAddedPlugins.remove(srcIndex);
                    mAddedPlugins.add(srcIndex, dstItem);
                    success = true;
                }
            } else {
                srcIndex = mNotAddedPlugins.indexOf(srcItem);
                dstIndex = mNotAddedPlugins.indexOf(dstItem);
                if (srcIndex != -1 && dstIndex != -1) {
                    Collections.swap(mNotAddedPlugins, srcIndex, dstIndex);
                    success = true;
                }
            }
        }
        if (success) {
            notifyDataSetChanged();
        }
        return success;
    }

    public void addItem(PluginItem item) {
        mNotAddedPlugins.remove(item);
        mAddedPlugins.remove(item);
        mAddedPlugins.add(item);
        notifyDataSetChanged();
    }

    public void removeItem(PluginItem item) {
        mNotAddedPlugins.remove(item);
        mAddedPlugins.remove(item);

        // 排在最前
        mNotAddedPlugins.add(0, item);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return 2 + mAddedPlugins.size() + mNotAddedPlugins.size();
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_TAG_ADDED;
        } else if (position == mAddedPlugins.size() + 1) {
            return TYPE_TAG_NOT_ADDED;
        } else if (position <= mAddedPlugins.size()) {
            return TYPE_PLUGIN_ADDED;
        } else {
            return TYPE_PLUGIN_NOT_ADDED;
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            switch (getItemViewType(position)) {
                case TYPE_TAG_ADDED:
                    convertView = View.inflate(mContext, R.layout.dl_added_item, null);
                    break;
                case TYPE_TAG_NOT_ADDED:
                    convertView = View.inflate(mContext, R.layout.dl_not_added_item, null);
                    break;
                case TYPE_PLUGIN_ADDED:
                    convertView = View.inflate(mContext, R.layout.dl_added_plugin_item, null);
                    viewHolder = new ViewHolder();
                    viewHolder.mIcon = (ImageView) convertView.findViewById(R.id.dl_plugin_icon);
                    viewHolder.mName = (TextView) convertView.findViewById(R.id.dl_plugin_name);
                    viewHolder.mState = (Button) convertView.findViewById(R.id.dl_plugin_state);
                    viewHolder.mMoveIcon = (ImageView) convertView.findViewById(R.id.dl_plugin_move);
                    convertView.setTag(viewHolder);
                    break;
                case TYPE_PLUGIN_NOT_ADDED:
                    convertView = View.inflate(mContext, R.layout.dl_not_added_plugin_item, null);
                    viewHolder = new ViewHolder();
                    viewHolder.mIcon = (ImageView) convertView.findViewById(R.id.dl_plugin_icon);
                    viewHolder.mName = (TextView) convertView.findViewById(R.id.dl_plugin_name);
                    viewHolder.mState = (Button) convertView.findViewById(R.id.dl_plugin_state);
                    convertView.setTag(viewHolder);
                    break;
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PluginItem item = getPlugin(position);

        switch (getItemViewType(position)) {
            case TYPE_TAG_ADDED:
                break;
            case TYPE_TAG_NOT_ADDED:
                break;
            case TYPE_PLUGIN_ADDED:
            case TYPE_PLUGIN_NOT_ADDED:
                viewHolder.mName.setText(item.mName);
                viewHolder.mState.setTag(item);
                viewHolder.mState.setOnClickListener(mListener);
                break;
        }
        ((ViewGroup) convertView).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        return convertView;
    }

    /**
     *
     */
    private class ViewHolder {
        public ImageView mIcon;
        public TextView mName;
        public Button mState;
        public ImageView mMoveIcon;
    }


}
