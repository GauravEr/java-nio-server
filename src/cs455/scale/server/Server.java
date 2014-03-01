package cs455.scale.server;

import cs455.scale.util.LoggingUtil;

/**
 * Author: Thilina
 * Date: 2/27/14
 * Main Class of the Server.
 */
public class Server {

    private final int port;
    private final ThreadPool threadPool;

    public Server(int port, int threadPoolSize) {
        this.port = port;
        this.threadPool = new ThreadPool(threadPoolSize);
    }

    public boolean initialize(){
        return threadPool.initialize();
    }

    /**
     * Temporary test method to test the thread pool
     */
    public void submitJobs(){
        for(int i = 0; i < 1000000; i++){
            Job job = new Job(i);
            threadPool.submitJob(job);
        }
    }

    public static void main(String[] args) {
        if(args.length < 2){
            LoggingUtil.logError("Missing required arguments. Expecting " +
                    "\'java cs455.scaling.server.Server port-num thread-pool-size\'");
            System.exit(-1);
        }
        int port = Integer.parseInt(args[0]);
        int threadPoolSize = Integer.parseInt(args[1]);

        // Server instance
        Server server = new Server(port, threadPoolSize);
        boolean initialized = server.initialize();
        if(initialized){
            LoggingUtil.logInfo("Server started successfully!");
        }
        server.submitJobs();
    }
}
