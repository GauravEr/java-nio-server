package cs455.scale.server;

import cs455.scale.server.task.Task;
import cs455.scale.util.LoggingUtil;
import cs455.scale.util.ThreadSafeQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Thread Pool implementation.
 * Author: Thilina
 * Date: 2/27/14
 */
public class ThreadPool{

    private final int size;
    private final List<Thread> workers;
    private final ThreadPoolManager threadPoolManager;
    private final CountDownLatch countDownLatch;
    private volatile boolean initialized;
    private ThreadSafeQueue<Worker> idleThreads;


    private class ThreadPoolManager extends Thread {

        @Override
        public void run() {
            JobQueue jobQueue = JobQueue.getInstance();
            // run scheduling
            while (true) {
                Task task = null;
                synchronized (jobQueue) {
                    if (jobQueue.hasJobs()) {
                        task = jobQueue.getNextJob();
                    } else {
                        try {
                            // wait till a job arrives
                            jobQueue.wait();
                            if (jobQueue.hasJobs()) {
                                task = jobQueue.getNextJob();
                            }
                        } catch (InterruptedException e) {
                            continue;
                        }
                    }
                    // job is available, schedule it only if free-threads available
                    if (task != null && !idleThreads.isEmpty()) {
                        Worker worker = idleThreads.remove();
                        worker.addJob(task);
                        jobQueue.remove(task);
                    }
                }
            }
        }
    }

    public ThreadPool(int size) {
        this.size = size;
        countDownLatch = new CountDownLatch(size);
        workers = new ArrayList<Thread>(size);
        idleThreads = new ThreadSafeQueue<Worker>();
        threadPoolManager = new ThreadPoolManager();
    }

    public boolean initialize() {
        threadPoolManager.start();
        for (int i = 0; i < size; i++) {
            workers.add(new Thread(new Worker(this)));
        }
        if (!initialized) {
            // start all threads in the thread-pool
            for (Thread t : workers) {
                t.start();
            }
            try {
                // wait till all the threads are started up.
                countDownLatch.await();
                initialized = true;
            } catch (InterruptedException e) {
                LoggingUtil.logError(this.getClass(), "Error starting the thread pool.", e);
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Acknowledge the completion of initialization. Add itself to idle thread pool.
     * @param worker
     */
    public void acknowledgeInit(Worker worker) {
        countDownLatch.countDown();
        idleThreads.add(worker);
    }

    /**
     * Acknowledge the completion of a job and return back to idle thread pool
     * @param worker Worker thread
     */
    public void acknowledgeCompletion(Worker worker) {
        idleThreads.add(worker);
    }

}
