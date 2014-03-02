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
public class ThreadPool extends Thread {

    private final int size;
    private final List<Thread> workers;
    private final CountDownLatch countDownLatch;
    private volatile boolean initialized;
    private Queue<Worker> idleThreads;

    // temp variables
    private AtomicLong submittedJobCount = new AtomicLong();
    private AtomicLong completedJobCount = new AtomicLong();

    class StatisticsThread implements Runnable {

        @Override
        public void run() {
            while(true){
                printStatistics();
                try {
                    Thread.sleep(5*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private StatisticsThread statisticsThread = new StatisticsThread();

    public ThreadPool(int size) {
        this.size = size;
        countDownLatch = new CountDownLatch(size);
        workers = new ArrayList<Thread>(size);
        idleThreads = new ConcurrentLinkedQueue<Worker>();
    }

    public boolean initialize(){
        this.start();
        new Thread(statisticsThread).start();
        for(int i = 0; i < size; i++){
            workers.add(new Thread(new Worker(this)));
        }
        if (!initialized) {
            for(Thread t : workers){
                t.start();
            }
            try {
                countDownLatch.await();
                initialized = true;
            } catch (InterruptedException e) {
                LoggingUtil.logError("Error starting the thread pool.", e);
                return false;
            }
            return true;
        }
        return false;
    }

    public void acknowledgeInit(Worker worker){
        countDownLatch.countDown();
        idleThreads.add(worker);
    }

    public void acknowledgeCompletion(Worker worker){
        idleThreads.add(worker);
        completedJobCount.incrementAndGet();
    }

    public void printStatistics(){
        LoggingUtil.logInfo("Submitted Jobs: " + submittedJobCount);
        LoggingUtil.logInfo("Completed Jobs: " + completedJobCount);
    }

    @Override
    public void run() {
        JobQueue jobQueue = JobQueue.getInstance();
        // run scheduling
        while (true){
            Task task = jobQueue.getNextJob();
            if(task != null && !idleThreads.isEmpty()){
                Worker worker = idleThreads.remove();
                worker.addJob(task);
            }
        }
    }
}
