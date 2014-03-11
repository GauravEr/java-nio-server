package cs455.scale.server;

import cs455.scale.server.task.Task;
import cs455.scale.util.LoggingUtil;
import cs455.scale.util.ThreadSafeQueue;

/**
 * Author: Thilina
 * Date: 2/28/14
 */
public class Worker implements Runnable {

    private final ThreadPool threadPool;
    private boolean initialized;
    private ThreadSafeQueue<Task> jobQueue = new ThreadSafeQueue<Task>();

    public Worker(ThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    public synchronized boolean addJob(Task task) {
        boolean notify = jobQueue.add(task);
        this.notifyAll();   // notify the worker thread about availability of jobs.
        return notify;
    }

    @Override
    public void run() {
        if (!initialized) {
            threadPool.acknowledgeInit(this);
            initialized = true;
            LoggingUtil.logInfo(this.getClass(),
                    "Worker Thread [" + Thread.currentThread().getId() + "] started successfully!");
        }
        while (true) {
            // use double checked locking to see if a job is assigned.
            if (jobQueue.isEmpty()) {
                synchronized (this) {
                    if (jobQueue.isEmpty()) {
                        try {
                            wait(); // it'll get notified when a job is added.
                        } catch (InterruptedException ignore) {
                            // ignore
                        }
                    }
                }
            }
            Task task = jobQueue.remove();
            task.complete();
            threadPool.acknowledgeCompletion(this);
        }
    }

}
