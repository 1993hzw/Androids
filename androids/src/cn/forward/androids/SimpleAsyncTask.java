package cn.forward.androids;

import android.os.Binder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import cn.forward.androids.utils.LogUtil;

/**
 * Created by huangziwei on 2017/3/29.
 */

public abstract class SimpleAsyncTask<Params, Progress, Result> {
    private static final String LOG_TAG = "SimpleAsyncTask";

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "SimpleAsyncTask #" + mCount.getAndIncrement());
        }
    };

    private static int sMaxSizeLIFO = 128;

    public enum Policy {
        FIFO, LIFO
    }

    //模拟栈的行为，后进先出
    public static class LinkedBlockingStack<T> extends LinkedBlockingDeque<T> {
        private static Policy sPolicy = Policy.FIFO;

        private LinkedBlockingStack(Policy policy) {
            sPolicy = policy;
        }

        @Override
        public boolean offer(T e) {
            switch (sPolicy) {
                case LIFO:
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

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
            KEEP_ALIVE, TimeUnit.MILLISECONDS,
            new LinkedBlockingStack<Runnable>(Policy.FIFO));

    private static final ThreadPoolExecutor EXECUTOR_LIFO = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
            KEEP_ALIVE, TimeUnit.MILLISECONDS,
            new LinkedBlockingStack<Runnable>(Policy.LIFO));

    // 按照优先级执行
    private static final ThreadPoolExecutor EXECUTOR_PRIORITY = new ThreadPoolExecutor(
            1, 1,
            1, TimeUnit.MILLISECONDS,
            new PriorityBlockingQueue<Runnable>() {
                @Override
                public boolean offer(Runnable runnable) {
                    return super.offer(new CompareRunnableFIFOWrapper((CompareRunnable) runnable));
                }
            });

    /**
     * 当优先级队列中的元素的优先级相等时,按照先进先出（FIFO）原则排序
     */
    private static class CompareRunnableFIFOWrapper implements CompareRunnable<CompareRunnableFIFOWrapper> {
        private static AtomicLong mCount = new AtomicLong(0);

        private final long mSecondPriority;
        private CompareRunnable mRunnable;

        private CompareRunnableFIFOWrapper(CompareRunnable runnable) {
            mRunnable = runnable;
            mSecondPriority = mCount.incrementAndGet();
        }

        public CompareRunnable getRunnable() {
            return mRunnable;
        }

        public long getSecondPriority() {
            return mSecondPriority;
        }

        @Override
        public void run() {
            mRunnable.run();
        }

        @Override
        public int compareTo(CompareRunnableFIFOWrapper another) {
            int res = mRunnable.compareTo(another.getRunnable());
            if (res == 0) {
                return mSecondPriority < another.getSecondPriority() ? -1 : 1;
            }
            return res;
        }
    }

    private static final int MESSAGE_POST_RESULT = 0x1;
    private static final int MESSAGE_POST_PROGRESS = 0x2;

    private static volatile Executor sDefaultExecutor = EXECUTOR;
    private static InternalHandler sHandler;

    private final WorkerRunnable<Params, Result> mWorker;
    private final FutureTask<Result> mFuture;

    private volatile Status mStatus = Status.PENDING;

    private final AtomicBoolean mCancelled = new AtomicBoolean();
    private final AtomicBoolean mTaskInvoked = new AtomicBoolean();

    public enum Status {
        PENDING,
        RUNNING,
        FINISHED,
    }

    private static Handler getHandler() {
        synchronized (SimpleAsyncTask.class) {
            if (sHandler == null) {
                sHandler = new InternalHandler();
            }
            return sHandler;
        }
    }

    public static void setDefaultExecutor(Executor exec) {
        sDefaultExecutor = exec;
    }

    public SimpleAsyncTask() {
        mWorker = new WorkerRunnable<Params, Result>() {
            public Result call() throws Exception {
                mTaskInvoked.set(true);

                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                //noinspection unchecked
                Result result = doInBackground(mParams);
                Binder.flushPendingCommands();
                return postResult(result);
            }
        };

        mFuture = new FutureTask<Result>(mWorker) {
            @Override
            protected void done() {
                try {
                    postResultIfNotInvoked(get());
                } catch (InterruptedException e) {
                    LogUtil.w(LOG_TAG, e);
                } catch (ExecutionException e) {
                    throw new RuntimeException("An error occurred while executing doInBackground()",
                            e.getCause());
                } catch (CancellationException e) {
                    postResultIfNotInvoked(null);
                }
            }
        };
    }

    /**
     * 重置任务，如果任务正在运行则抛出异常
     */
    public boolean reset() {
        if (Status.RUNNING == mStatus) {
            return false;
        } else {
            mStatus = Status.PENDING;
            mCancelled.set(false);
            mTaskInvoked.set(false);
            return true;
        }
    }

    private void postResultIfNotInvoked(Result result) {
        final boolean wasTaskInvoked = mTaskInvoked.get();
        if (!wasTaskInvoked) {
            postResult(result);
        }
    }

    private Result postResult(Result result) {
        @SuppressWarnings("unchecked")
        Message message = getHandler().obtainMessage(MESSAGE_POST_RESULT,
                new AsyncTaskResult<Result>(this, result));
        message.sendToTarget();
        return result;
    }

    public final Status getStatus() {
        return mStatus;
    }


    protected abstract Result doInBackground(Params... params);


    protected void onPreExecute() {
    }


    protected void onPostExecute(Result result) {
    }

    protected void onProgressUpdate(Progress... values) {
    }


    protected void onCancelled(Result result) {
        onCancelled();
    }

    protected void onCancelled() {
    }


    public final boolean isCancelled() {
        return mCancelled.get();
    }


    public final boolean cancel(boolean mayInterruptIfRunning) {
        mCancelled.set(true);
        return mFuture.cancel(mayInterruptIfRunning);
    }


    public final Result get() throws InterruptedException, ExecutionException {
        return mFuture.get();
    }


    public final Result get(long timeout, TimeUnit unit) throws InterruptedException,
            ExecutionException, TimeoutException {
        return mFuture.get(timeout, unit);
    }

    public final SimpleAsyncTask<Params, Progress, Result> execute(Params... params) {
        return executeOnExecutor(sDefaultExecutor, params);
    }

    public final SimpleAsyncTask<Params, Progress, Result> executeLIFO(Params... params) {
        return executeOnExecutor(EXECUTOR_LIFO, params);
    }

    /**
     * 按照优先级执行
     *
     * @param priority 优先级
     * @param params
     * @return
     */
    public final SimpleAsyncTask<Params, Progress, Result> executePriority(Priority priority, Params... params) {
        if (priority == null) {
            throw new RuntimeException("priority is null!");
        }
        return executeOnExecutor(EXECUTOR_PRIORITY, priority, params);
    }

    public final SimpleAsyncTask<Params, Progress, Result> executeOnExecutor(Executor exec, Params... params) {
        return executeOnExecutor(exec, null, params);
    }

    private final SimpleAsyncTask<Params, Progress, Result> executeOnExecutor(Executor exec, Priority priority,
                                                                              Params... params) {
        if (mStatus != Status.PENDING) {
            switch (mStatus) {
                case RUNNING:
                    throw new IllegalStateException("Cannot execute task:"
                            + " the task is already running.");
                case FINISHED:
                    throw new IllegalStateException("Cannot execute task:"
                            + " the task has already been executed "
                            + "(a task can be executed only once)");
            }
        }

        mStatus = Status.RUNNING;

        onPreExecute();

        mWorker.mParams = params;

        if (priority != null) {
            exec.execute(new PriorityRunnable(priority, mFuture));
        } else {
            exec.execute(mFuture);
        }

        return this;
    }


    public static void execute(Runnable runnable) {
        sDefaultExecutor.execute(runnable);
    }


    protected final void publishProgress(Progress... values) {
        if (!isCancelled()) {
            getHandler().obtainMessage(MESSAGE_POST_PROGRESS,
                    new AsyncTaskResult<Progress>(this, values)).sendToTarget();
        }
    }

    private void finish(Result result) {
        if (isCancelled()) {
            onCancelled(result);
        } else {
            onPostExecute(result);
        }
        mStatus = Status.FINISHED;
    }

    private static class InternalHandler extends Handler {
        public InternalHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            AsyncTaskResult<?> result = (AsyncTaskResult<?>) msg.obj;
            switch (msg.what) {
                case MESSAGE_POST_RESULT:
                    // There is only one result
                    result.mTask.finish(result.mData[0]);
                    break;
                case MESSAGE_POST_PROGRESS:
                    result.mTask.onProgressUpdate(result.mData);
                    break;
            }
        }
    }

    private static abstract class WorkerRunnable<Params, Result> implements Callable<Result> {
        Params[] mParams;
    }

    private static class AsyncTaskResult<Data> {
        final SimpleAsyncTask mTask;
        final Data[] mData;

        AsyncTaskResult(SimpleAsyncTask task, Data... data) {
            mTask = task;
            mData = data;
        }
    }

}

