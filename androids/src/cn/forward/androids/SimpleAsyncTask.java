package cn.forward.androids;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.forward.androids.utils.ThreadUtil;

/*
 * 自定义的AsyncTask，任务队列采用后进先出的策略
 */
abstract public class SimpleAsyncTask<Params, Progress, Result> {

    private static int sMaxSizeLIFO = 128;

    public static int getMaxSizeLIFO() {
        return sMaxSizeLIFO;
    }

    /**
     * 队列的最大长度，只对后进先出队列有效，当超过这个最大值时，新加入的任务会把队列末尾的任务剔除
     */
    public static void setMaxSizeLIFO(int max) {
        sMaxSizeLIFO = max;
    }


    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT + 1;
    private static final int KEEP_ALIVE = 1;

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
            KEEP_ALIVE, TimeUnit.MILLISECONDS,
            new LinkedBlockingStack<Runnable>(LinkedBlockingStack.POLICY_FIFO));

    private static final ThreadPoolExecutor executorLIFO = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
            KEEP_ALIVE, TimeUnit.MILLISECONDS,
            new LinkedBlockingStack<Runnable>(LinkedBlockingStack.POLICY_LIFO));

    private ThreadUtil mThreadUtil = ThreadUtil.getInstance();

    public SimpleAsyncTask() {
    }

    protected Result doInBackground(Params... params) {
        return null;
    }

    protected void onProgressUpdate(Progress... progress) {
    }

    abstract protected void onPostExecute(Result result);

    protected void publishProgress(final Progress... progress) {
        mThreadUtil.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                onProgressUpdate(progress);
            }
        });
    }

    public void execute(final Params... params) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                final Result result = doInBackground(params);
                mThreadUtil.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        onPostExecute(result);
                    }
                });
            }
        });
    }

    public void executeLIFO(final Params... params) {
        executorLIFO.execute(new Runnable() {
            @Override
            public void run() {
                final Result result = doInBackground(params);
                mThreadUtil.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        onPostExecute(result);
                    }
                });
            }
        });
    }

    public void clearAll() {
        executor.getQueue().clear();
    }

    public boolean remove(Runnable runnable) {
        return executor.remove(runnable);
    }

    public void clearAllLIFO() {
        executorLIFO.getQueue().clear();
    }

    public boolean removeLIFO(Runnable runnable) {
        return executorLIFO.remove(runnable);
    }

    //模拟栈的行为，后进先出
    private static class LinkedBlockingStack<T> extends LinkedBlockingDeque<T> {

        public static final int POLICY_FIFO = 1; // 先进先出
        public static final int POLICY_LIFO = 2; // 后进先出
        private static int sPolicy = POLICY_FIFO;

        private LinkedBlockingStack(int policy) {
            sPolicy = policy;
        }

        @Override
        public boolean offer(T e) {

            switch (sPolicy) {
                case POLICY_LIFO:
                    offerFirst(e);
                    if (size() > sMaxSizeLIFO) {
                        removeLast(); // 移除末尾的队列
                    }
                    return true;
                default:
                    offerLast(e);
                    return true;

            }
        }
    }
}
