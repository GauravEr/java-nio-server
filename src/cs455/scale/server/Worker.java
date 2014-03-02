package cs455.scale.server;

import cs455.scale.server.task.Task;
import cs455.scale.util.LoggingUtil;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Author: Thilina
 * Date: 2/28/14
 */
public class Worker implements Runnable {

    private final ThreadPool threadPool;
    private boolean initialized;
    private Queue<Task> jobQueue = new ConcurrentLinkedQueue<Task>();

    public Worker(ThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    public boolean addJob(Task task){
        return jobQueue.add(task);
    }

    @Override
    public void run() {
        if(!initialized){
            threadPool.acknowledgeInit(this);
            initialized = true;
            LoggingUtil.logInfo("Thread [" + Thread.currentThread().getId() + "] started successfully!");
        }
        while(true){
            if(!jobQueue.isEmpty()){
                Task task = jobQueue.remove();
                task.complete();
                threadPool.acknowledgeCompletion(this);
            }
        }
    }

}
