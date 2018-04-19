package cn.forward.androids.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 用于RecyclerView的适配器
 */
public abstract class EasyAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<EasyAdapter.SelectionViewHolder<VH>> {

    /**
     * 作用等同于RecyclerView.Adapter.onCreateViewHolder()
     */
    public abstract VH whenCreateViewHolder(ViewGroup parent, int viewType);

    /**
     * 作用等同于RecyclerView.Adapter.onBindViewHolder()
     */
    public abstract void whenBindViewHolder(VH holder, int position);

    /**
     * @param mode              可区分点击模式、单选和多选模式
     * @param maxSelectionCount 用于多选模式，设置最大的选择数量，maxSelectionCount<=0 表示不限制选择数
     */
    public EasyAdapter(Context context, Mode mode, int maxSelectionCount) {
        this.context = context;
        this.mode = mode;
        this.maxSelectionCount = maxSelectionCount;
    }

    public EasyAdapter(Context context) {
        this(context, Mode.CLICK, -1);
    }

    private Context context;

    /**
     * 最大可选的数量，maxSelectionCount<=0表示不限制选择数量
     */
    private int maxSelectionCount;

    public void setMaxSelectionCount(int maxSelectionCount) {
        this.maxSelectionCount = maxSelectionCount;
        if (maxSelectionCount > 0) {
           /* while (selectedSet.size() > maxSelectionCount) {
                selectedSet.removeLast();
            }*/
            if (selectedSet.size() > maxSelectionCount) {
                selectedSet.clear();
            }
        }
        notifyDataSetChanged();
    }

    public int getMaxSelectionCount() {
        return maxSelectionCount;
    }

    /**
     * 当前模式
     */
    private Mode mode;

    public void setMode(Mode mode) {
        if (this.mode == mode) {
            return;
        }
        Mode old = this.mode;
        this.mode = mode;
        if (onModeChangedListener != null) {
            onModeChangedListener.onModeChanged(old, mode);
        }
        notifyDataSetChanged();
    }

    /**
     * 单选项的索引
     */
    private int singleSelectedPosition = 0;

    public void setSingleSelectedPosition(int singleSelectedPosition) {
        if (this.singleSelectedPosition == singleSelectedPosition) {
            return;
        }
        this.singleSelectedPosition = singleSelectedPosition;
        if (onSingleSelectListener != null) {
            onSingleSelectListener.onSelected(singleSelectedPosition);
        }
        notifyDataSetChanged();


    }

    private OnItemClickedListener onItemClickedListener = null;
    private OnItemLongClickedListener onItemLongClickedListener = null;
    private OnSingleSelectListener onSingleSelectListener = null;
    private OnMultiSelectListener onMultiSelectListener = null;
    private OnModeChangedListener onModeChangedListener = null;

    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }

    public OnItemClickedListener getOnItemClickedListener() {
        return onItemClickedListener;
    }

    public void setOnItemLongClickedListener(OnItemLongClickedListener onItemLongClickedListener) {
        this.onItemLongClickedListener = onItemLongClickedListener;
    }

    public OnItemLongClickedListener getOnItemLongClickedListener() {
        return onItemLongClickedListener;
    }

    public void setOnSingleSelectListener(OnSingleSelectListener onSingleSelectListener) {
        this.onSingleSelectListener = onSingleSelectListener;
    }

    public int getSingleSelectedPosition() {
        return singleSelectedPosition;
    }

    public void setOnMultiSelectListener(OnMultiSelectListener onMultiSelectListener) {
        this.onMultiSelectListener = onMultiSelectListener;
    }

    public OnMultiSelectListener getOnMultiSelectListener() {
        return onMultiSelectListener;
    }

    public void setOnModeChangedListener(OnModeChangedListener onModeChangedListener) {
        this.onModeChangedListener = onModeChangedListener;
    }

    public OnModeChangedListener getOnModeChangedListener() {
        return onModeChangedListener;
    }

    /**
     * 记录已选择的item
     */
    private LinkedHashSet<Integer> selectedSet = new LinkedHashSet<Integer>();

    @Override
    public SelectionViewHolder<VH> onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = whenCreateViewHolder(parent, viewType);
        // 获取外部创建的ViewHolder,进行进一步的封装
        final SelectionViewHolder viewHolder = new SelectionViewHolder(vh);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = viewHolder.getAdapterPosition();
                if (mode == Mode.CLICK) {
                    if (onItemClickedListener != null) {
                        onItemClickedListener.onClicked(pos);
                    }
                } else if (mode == Mode.SINGLE_SELECT) {
                    singleSelectedPosition = pos;
                    if (onSingleSelectListener != null) {
                        onSingleSelectListener.onSelected(singleSelectedPosition);
                    }
                    notifyDataSetChanged();
                } else if (mode == Mode.MULTI_SELECT) {
                    if (maxSelectionCount > 0 &&
                            selectedSet.size() >= maxSelectionCount // 达到限制
                            && !selectedSet.contains(pos)) { // 且选择新的一项
                        if (onMultiSelectListener != null) {
                            onMultiSelectListener.onOutOfMax(pos);
                        }
                        return;
                    }
                    boolean isSelected = selectedSet.contains(pos);
                    if (isSelected) {
                        selectedSet.remove(pos);
                    } else {
                        selectedSet.add(pos);
                    }
                    if (onMultiSelectListener != null) {
                        onMultiSelectListener.onSelected(pos, !isSelected);
                    }
                    notifyDataSetChanged();
                }
            }
        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int pos = viewHolder.getAdapterPosition();
                if (onItemLongClickedListener != null) {
                    return onItemLongClickedListener.onLongClicked(pos);
                }
                return false;
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SelectionViewHolder<VH> holder, int position) {
        whenBindViewHolder(holder.viewHolder, position);
        switch (mode) {
            case CLICK:
                holder.itemView.setSelected(false);
                break;
            case SINGLE_SELECT:
                holder.itemView.setSelected(singleSelectedPosition == position);
                break;
            case MULTI_SELECT:
                holder.itemView.setSelected(selectedSet.contains(position));
                break;
        }

    }

    /**
     * 全选，，只在maxSelection不限制可选数时有效
     */
    public void selectAll() {
        if (maxSelectionCount > 0) {
            return;
        }

        for (int i = 0; i < getItemCount(); i++) {
            selectedSet.add(i);
        }
        if (onMultiSelectListener != null) {
            onMultiSelectListener.onSelected(SelectionMode.SELECT_ALL, new LinkedHashSet(selectedSet));
        }
        notifyDataSetChanged();
    }

    public void unselectAll() {
        selectedSet.clear();
        if (onMultiSelectListener != null) {
            onMultiSelectListener.onSelected(SelectionMode.UNSELECT_ALL, new LinkedHashSet(selectedSet));
        }
        notifyDataSetChanged();
    }

    /**
     * 反选，只在maxSelection不限制可选数时有效
     */
    public void reverseSelected() {
        if (maxSelectionCount > 0) {
            return;
        }

        Set set = new HashSet(selectedSet);
        for (int i = 0; i < getItemCount(); i++) {
            selectedSet.add(i);
        }
        selectedSet.removeAll(set);
        if (onMultiSelectListener != null) {
            onMultiSelectListener.onSelected(SelectionMode.REVERSE_SELECTED, new LinkedHashSet(selectedSet));
        }
        notifyDataSetChanged();
    }

    /**
     * 选中某项。单选时只有position[0]才生效
     */
    public void select(int... position) {
        if (mode == Mode.SINGLE_SELECT) { // 单选
            singleSelectedPosition = position[0];
            if (onSingleSelectListener != null) {
                onSingleSelectListener.onSelected(singleSelectedPosition);
            }
        } else {
            for (int p : position) {
                if (p >= getItemCount() || selectedSet.contains(p)) {
                    continue;
                }
                if (onMultiSelectListener != null) {
                    if (maxSelectionCount > 0 && selectedSet.size() >= maxSelectionCount) {
                        onMultiSelectListener.onOutOfMax(p);
                    } else {
                        selectedSet.add(p);
                        onMultiSelectListener.onSelected(p, false);
                    }
                } else {
                    if (maxSelectionCount >= 1 && selectedSet.size() >= maxSelectionCount) {

                    } else {
                        selectedSet.add(p);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    public void unselect(int... position) {
        for (int p : position) {
            if (p >= getItemCount() || !selectedSet.contains(p)) {
                continue;
            }
            if (onMultiSelectListener != null) {
                selectedSet.remove(p);
                onMultiSelectListener.onSelected(p, false);
            } else {
                selectedSet.remove(p);
            }
        }
        notifyDataSetChanged();
    }


    public boolean isSelected(int position) {
        return selectedSet.contains(position);
    }

    public Set<Integer> getSelectedSet() {
        return new LinkedHashSet<Integer>(selectedSet);
    }

    /**
     * 可区分点击模式、单选和多选模式
     */
    public enum Mode {
        CLICK, SINGLE_SELECT, MULTI_SELECT
    }

    static class SelectionViewHolder<VH extends RecyclerView.ViewHolder> extends RecyclerView.ViewHolder {
        VH viewHolder;

        public SelectionViewHolder(VH viewHolder) {
            super(new SelectionItemView(viewHolder));
            this.viewHolder = viewHolder;
        }
    }

    private static class SelectionItemView extends FrameLayout {
        public SelectionItemView(RecyclerView.ViewHolder viewHolder) {
            super(viewHolder.itemView.getContext());
            addView(viewHolder.itemView);
        }
    }

    /**
     * 点击item的监听器
     */
    public interface OnItemClickedListener {
        void onClicked(int position);
    }

    /**
     * 长按item的监听器
     */
    public interface OnItemLongClickedListener {
        boolean onLongClicked(int position);
    }

    /**
     * 单选的监听器
     */
    public interface OnSingleSelectListener {
        void onSelected(int position);
    }

    /**
     * 多选操作
     */
    public enum SelectionMode {
        SELECT_ALL, // 全选
        UNSELECT_ALL, // 全不选
        REVERSE_SELECTED, // 反选
    }

    /**
     * 多选的监听器
     */
    public interface OnMultiSelectListener {

        /**
         * 选择一个时的回调
         *
         * @param position   选择的索引位置
         * @param isSelected true为选中，false取消选中
         */
        void onSelected(int position, boolean isSelected);

        /**
         * 复杂多选操作的回调
         *
         * @param selectionMode 多选操作类型
         * @param selectedSet   选中的集合
         */
        void onSelected(SelectionMode selectionMode, Set<Integer> selectedSet);

        /**
         * 超出最大选择数量时回调
         */
        void onOutOfMax(int position);
    }

    /**
     * 模式改变监听
     */
    public interface OnModeChangedListener {
        void onModeChanged(Mode oldMode, Mode newMode);
    }

}

