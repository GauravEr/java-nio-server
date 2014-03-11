package cs455.scale.server.task;

import cs455.scale.server.Server;

import java.nio.channels.SelectionKey;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstract task which is extended by Read/Write tasks.
 * Author: Thilina
 * Date: 3/4/14
 */
public abstract class AbstractTask implements Task {
    public static AtomicInteger jobCounter = new AtomicInteger(0);

    protected SelectionKey selectionKey;
    protected Server server;
    protected int jobId;

    protected AbstractTask(SelectionKey selectionKey, Server server) {
        this.selectionKey = selectionKey;
        this.server = server;
        jobId = jobCounter.incrementAndGet();
    }
}
