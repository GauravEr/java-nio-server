package cs455.scale.server;

import cs455.scale.server.task.Task;
import cs455.scale.util.ThreadSafeLinkedQueue;

/**
 * Author: Thilina
 * Date: 3/2/14
 */
public class JobQueue {

    private static final JobQueue instance = new JobQueue();

    private final ThreadSafeLinkedQueue<Task> jobs = new ThreadSafeLinkedQueue<Task>();

    private JobQueue() {
        // singleton
    }

    public static JobQueue getInstance() {
        return instance;
    }

    public synchronized void addJob(Task task) {
        jobs.add(task);
        this.notifyAll();
    }

    public synchronized boolean hasJobs(){
        return !jobs.isEmpty();
    }

    public Task getNextJob() {
        if (!jobs.isEmpty()) {
            return jobs.peek();
        }
        return null;
    }

    public void remove(Task task){
        jobs.remove(task);
    }

}
