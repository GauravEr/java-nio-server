package cs455.scale.server;

import cs455.scale.server.task.Task;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Author: Thilina
 * Date: 3/2/14
 */
public class JobQueue {

    private static final JobQueue instance = new JobQueue();

    private final Queue<Task> jobs = new ConcurrentLinkedQueue<Task>();

    private JobQueue() {
        // singleton
    }

    public static JobQueue getInstance() {
        return instance;
    }

    public void addJob(Task task) {
        jobs.add(task);
    }

    public Task getNextJob() {
        if (!jobs.isEmpty()) {
            return jobs.remove();
        }
        return null;
    }

}
