package cs455.scale.server;

import cs455.scale.util.LoggingUtil;

import java.util.concurrent.CountDownLatch;

/**
 * Author: Thilina
 * Date: 2/28/14
 */
public class Job implements Runnable {

    private final CountDownLatch countDownLatch;
    private boolean initialized;

    public Job(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }


    @Override
    public void run() {
        if(!initialized){
            countDownLatch.countDown();
            initialized = true;
            LoggingUtil.logInfo("Thread [" + Thread.currentThread().getId() + "] started successfully!");
        }
        while(true){

        }
    }
}
