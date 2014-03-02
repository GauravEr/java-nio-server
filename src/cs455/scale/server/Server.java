package cs455.scale.server;

import cs455.scale.server.task.ConnectionAcceptTask;
import cs455.scale.server.task.ReadTask;
import cs455.scale.util.LoggingUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

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

        try {
            ServerSocketChannel ssc = ServerSocketChannel.open();

            ssc.configureBlocking(false);

            ServerSocket ss = ssc.socket();
            InetSocketAddress isa = new InetSocketAddress(server.port);
            ss.bind(isa);

            Selector selector = Selector.open();

            ssc.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Listening on port " + port);
            JobQueue jobQueue = JobQueue.getInstance();

            while(true){
                int num = selector.select();

                if (num == 0) {
                    continue;
                }

                Set keys = selector.selectedKeys();
                Iterator it = keys.iterator();
                while (it.hasNext()) {

                    SelectionKey key = (SelectionKey) it.next();

                    if ((key.readyOps() & SelectionKey.OP_ACCEPT) ==
                            SelectionKey.OP_ACCEPT) {
                        ConnectionAcceptTask connAcceptTask = new ConnectionAcceptTask(selector, ss);
                        jobQueue.addJob(connAcceptTask);
                    } else if((key.readyOps() & SelectionKey.OP_READ) ==
                            SelectionKey.OP_READ){
                        ReadTask readTask = new ReadTask(key);
                        jobQueue.addJob(readTask);
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
