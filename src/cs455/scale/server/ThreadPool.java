package cs455.scale.server;

import cs455.scale.util.LoggingUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Author: Thilina
 * Date: 2/27/14
 */
public class ThreadPool {

    private final int size;
    private final List<Thread> workers;
    private final CountDownLatch countDownLatch;
    private volatile boolean initialized;

    public ThreadPool(int size) {
        this.size = size;
        countDownLatch = new CountDownLatch(size);
        List<Thread> threads = new ArrayList<Thread>(size);
        for(int i = 0; i < size; i++){
            threads.add(new Thread(new Job(countDownLatch)));
        }
        workers = Collections.unmodifiableList(threads);
    }

    public boolean initialize(){
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

}
