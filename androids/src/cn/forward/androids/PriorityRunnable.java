package cn.forward.androids;

/**
 *
 * Created by huangziwei on 2017/3/30.
 */

public class PriorityRunnable implements Runnable, Comparable<PriorityRunnable> {
    private final Priority mPriority;
    private final Runnable mRunnable;

    public PriorityRunnable(Priority priority) {
        this(priority, null);
    }

    public PriorityRunnable(Priority priority, Runnable runnable) {
        mPriority = priority == null ? Priority.DEFAULT : priority;
        mRunnable = runnable;
    }

    @Override
    public int compareTo(PriorityRunnable another) {
        // 如果优先级相等，则按照后进先出的原则排序
        return this.mPriority.ordinal() <= another.mPriority.ordinal()
                ? -1 : 1;
    }

    @Override
    public void run() {
        if (mRunnable != null) {
            mRunnable.run();
        }
    }
}
