package cs455.scale.server;

import cs455.scale.server.task.Task;
import cs455.scale.util.ThreadSafeQueue;

/**
 * The implementation of a Job Queue where selector thread
 * will be submitting jobs.
 * Thread pool manager will continuously query for new jobs and schedule them
 * to one of the idling threads from the thread pool.
 * This class is a singleton.
 * Author: Thilina
 * Date: 3/2/14
 */
public class JobQueue {

    private static final JobQueue instance = new JobQueue();

    private final ThreadSafeQueue<Task> jobs = new ThreadSafeQueue<Task>();

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
