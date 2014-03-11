package cs455.scale.server;

import cs455.scale.server.task.Task;
import cs455.scale.util.LoggingUtil;
import cs455.scale.util.ThreadSafeLinkedQueue;

/**
 * Author: Thilina
 * Date: 2/28/14
 */
public class Worker implements Runnable {

    private final ThreadPool threadPool;
    private boolean initialized;
    private ThreadSafeLinkedQueue<Task> jobQueue = new ThreadSafeLinkedQueue<Task>();

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
            LoggingUtil.logInfo(this.getClass(),
                    "Worker Thread [" + Thread.currentThread().getId() + "] started successfully!");
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
