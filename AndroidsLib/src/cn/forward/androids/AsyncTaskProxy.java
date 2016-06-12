package cn.forward.androids;

import android.os.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by huangziwei on 16-5-10.
 */
public class AsyncTaskProxy {

    public static int POLICY_FIFO = 1; // 先进先出
    public static int POLICY_LIFO = 2; // 后进先出
    private static int sPolicy = POLICY_LIFO;
    /**
     * 队列的最大长度，只对后进先出队列有效，当超过这个最大值时，新加入的任务会把队列末尾的任务剔除
     */
    private static int sMaxSizeLIFO = 128;

    public static int getMaxSizeLIFO() {
        return sMaxSizeLIFO;
    }

    public static void setMaxSizeLIFO(int max) {
        sMaxSizeLIFO = max;
    }

    public static void setPolicy(int policy) {
        sPolicy = policy;
    }

    public static int getPolicy() {
        return sPolicy;
    }

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;

    private static final LinkedBlockingStack<Runnable> sQueue = new LinkedBlockingStack<Runnable>();

    private static final ExecutorService executor = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
            KEEP_ALIVE, TimeUnit.MILLISECONDS, sQueue);

    public static <Params, Progress, Result> android.os.AsyncTask<Params, Progress, Result> execute(
            android.os.AsyncTask<Params, Progress, Result> task, Params... objs) {
        task.executeOnExecutor(executor, objs);
        return task;
    }

    public static void clear() {
        sQueue.clear();
    }




    //模拟栈的行为，后进先出
    private static class LinkedBlockingStack<T> extends LinkedBlockingDeque<T> {
        @Override
        public boolean offer(T e) {
            if (sPolicy == POLICY_FIFO) {
                offerLast(e);
            } else {
                offerFirst(e);
                if (size() > sMaxSizeLIFO) {
                    removeLast(); // 移除末尾的队列
                }
            }
            return true;
        }

        @Override
        public T poll() {
            return super.poll();
        }
    }

    private class RunnableWithTag implements Runnable {

        @Override
        public void run() {

        }
    }
}
