package cs455.scale.server;

import cs455.scale.server.task.Task;
import cs455.scale.util.LoggingUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Author: Thilina
 * Date: 2/27/14
 */
public class ThreadPool{

    private final int size;
    private final List<Thread> workers;
    private final ThreadPoolManager threadPoolManager;
    private final CountDownLatch countDownLatch;
    private volatile boolean initialized;
    private Queue<Worker> idleThreads;

    // temp variables
    private AtomicLong submittedJobCount = new AtomicLong();
    private AtomicLong completedJobCount = new AtomicLong();

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
                        System.out.println("Got the job" + task.getClass());
                    } else {
                        try {
                            jobQueue.wait();
                            if (jobQueue.hasJobs()) {
                                task = jobQueue.getNextJob();
                            }
                        } catch (InterruptedException e) {
                            continue;
                        }
                    }
                    if (!idleThreads.isEmpty()) {
                        System.out.println("Schuedled the job" + task.getClass());
                        Worker worker = idleThreads.remove();
                        worker.addJob(task);
                    }
                }
            }
        }
    }

    public ThreadPool(int size) {
        this.size = size;
        countDownLatch = new CountDownLatch(size);
        workers = new ArrayList<Thread>(size);
        idleThreads = new ConcurrentLinkedQueue<Worker>();
        threadPoolManager = new ThreadPoolManager();
    }

    public boolean initialize() {
        threadPoolManager.start();
        //new Thread(statisticsThread).start();
        for (int i = 0; i < size; i++) {
            workers.add(new Thread(new Worker(this)));
        }
        if (!initialized) {
            for (Thread t : workers) {
                t.start();
            }
            try {
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

    public void acknowledgeInit(Worker worker) {
        countDownLatch.countDown();
        idleThreads.add(worker);
    }

    public void acknowledgeCompletion(Worker worker) {
        idleThreads.add(worker);
        completedJobCount.incrementAndGet();
    }

}
