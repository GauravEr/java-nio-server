package cs455.scale.server;

import java.util.Random;

/**
 * Author: Thilina
 * Date: 2/28/14
 */
public class Job {

    int id;

    public Job(int id) {
        this.id = id;
    }

    public void complete(){
        long tid = Thread.currentThread().getId();
//        System.out.println("Thread[" + tid + "] started the job " + id);
        Random rand = new Random(System.currentTimeMillis());
        int length = rand.nextInt(4);
        try {
            Thread.sleep(length * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        System.out.println("Thread[" + tid + "] completed the job " + id);
    }
}
