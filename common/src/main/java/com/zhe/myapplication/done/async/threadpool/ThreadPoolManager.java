package com.zhe.myapplication.done.async.threadpool;

import android.util.Log;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池管理类
 * Created by zhe on 2016/12/9.
 */
public class ThreadPoolManager {

    private static final String TAG = "ThreadPoolManager";

    /**
     * 线程池的大小
     */
    private int poolSize;
    private static final int MIN_POOL_SIZE = 1;
    private static final int MAX_POOL_SIZE = 10;

    /**
     * 线程池
     */
    private ExecutorService threadPool;

    /**
     * 请求队列
     */
    private LinkedList<ThreadPoolTask> asyncTasks;

    /**
     * 工作方式
     */
    private int type;
    public static final int TYPE_FIFO = 0;
    public static final int TYPE_LIFO = 1;

    /**
     * 轮询线程
     */
    private Thread poolThread;
    /**
     * 轮询时间
     */
    private static final int SLEEP_TIME = 200;

    public ThreadPoolManager(int type, int poolSize) {
        this.type = (type == TYPE_FIFO) ? TYPE_FIFO : TYPE_LIFO;

        if (poolSize < MIN_POOL_SIZE) poolSize = MIN_POOL_SIZE;
        if (poolSize > MAX_POOL_SIZE) poolSize = MAX_POOL_SIZE;
        this.poolSize = poolSize;

        threadPool = Executors.newFixedThreadPool(this.poolSize);

        asyncTasks = new LinkedList<>();
    }

    /**
     * 向任务队列中添加任务
     *
     * @param task
     */
    public void addAsyncTask(ThreadPoolTask task) {
        synchronized (asyncTasks) {
            Log.i(TAG, "add task: " + task.getURL());
            asyncTasks.addLast(task);
        }
    }

    /**
     * 开启线程池轮询
     *
     * @return
     */
    public void start() {
        if (poolThread == null) {
            poolThread = new Thread(new PoolRunnable());
            poolThread.start();
        }
    }

    /**
     * 结束轮询，关闭线程池
     */
    public void stop() {
        poolThread.interrupt();
        poolThread = null;
    }

    /**
     * 从任务队列中提取任务
     *
     * @return
     */
    private ThreadPoolTask getAsyncTask() {
        synchronized (asyncTasks) {
            if (asyncTasks.size() > 0) {
                ThreadPoolTask task = (this.type == TYPE_FIFO) ?
                        asyncTasks.removeFirst() : asyncTasks.removeLast();
                return task;
            }
        }
        return null;
    }

    /**
     * 实现轮询的Runnable
     */
    private class PoolRunnable implements Runnable {

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    ThreadPoolTask task = getAsyncTask();
                    if (task == null) {
                        try {
                            Thread.sleep(SLEEP_TIME);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        continue;
                    }
                    threadPool.execute(task);
                }
            } finally {
                threadPool.shutdown();
            }
        }

    }
}