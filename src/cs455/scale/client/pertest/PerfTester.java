package cs455.scale.client.pertest;

import cs455.scale.client.Client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author: Thilina
 * Date: 3/9/14
 */
public class PerfTester implements Runnable{

    private final Client client;

    public PerfTester(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        /*Random rand = new Random(System.currentTimeMillis());
        try {
            Thread.sleep(rand.nextInt(5)*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        client.initialize();
        client.start();
    }

    public static void main(String[] args) {
        String serverHost = args[0];
        int serverPort = Integer.parseInt(args[1]);
        int mRate = Integer.parseInt(args[2]);

        ExecutorService tPool = Executors.newFixedThreadPool(100);
        for(int i = 0; i < 100; i++){
            Client client = new Client(serverHost, serverPort, mRate);
            PerfTester perfTester = new PerfTester(client);
            tPool.submit(perfTester);
        }
        System.out.println("All jobs are submitted to thread pool.");
    }

}
