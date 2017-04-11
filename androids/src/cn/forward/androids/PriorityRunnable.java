package cn.forward.androids;

/**
 * Created by huangziwei on 2017/3/30.
 */

public class PriorityRunnable implements CompareRunnable<PriorityRunnable> {
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
        if (this.mPriority.ordinal() < another.mPriority.ordinal()) {
            return -1;
        } else if (this.mPriority.ordinal() > another.mPriority.ordinal()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public void run() {
        if (mRunnable != null) {
            mRunnable.run();
        }
    }
}
