package cn.forward.androids;

import android.os.Handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/*
 * 自定义的AsynTask，任务队列采用后进先出的策略
 */
abstract public class AsyncTask<Params, Progress, Result> {
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

    private static final int THREADS = 3;//线程数
    private Handler mHandler;

    private static final ExecutorService executor = new ThreadPoolExecutor(
            THREADS, THREADS,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingStack<Runnable>());

    public AsyncTask() {
        mHandler = new Handler();
    }

    protected Result doInBackground(Params... params) {
        return null;
    }

    protected void onProgressUpdate(Progress... progress) {
    }

    abstract protected void onPostExecute(Result result);

    protected void publishProgress(final Progress... progress) {
        mHandler.post(new Runnable() {
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
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onPostExecute(result);
                    }
                });
            }
        });
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
    }

}
