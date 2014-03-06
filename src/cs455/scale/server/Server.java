package cs455.scale.server;

import cs455.scale.server.task.ConnectionAcceptTask;
import cs455.scale.server.task.ReadTask;
import cs455.scale.server.task.WriteTask;
import cs455.scale.util.LoggingUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Author: Thilina
 * Date: 2/27/14
 * Main Class of the Server.
 */
public class Server {

    private final int port;
    private final ThreadPool threadPool;
    private Queue<ServerChannelChange> channelChanges = new ConcurrentLinkedQueue<ServerChannelChange>();
    private Selector selector;

    public Server(int port, int threadPoolSize) {
        this.port = port;
        this.threadPool = new ThreadPool(threadPoolSize);
    }

    public boolean initialize() throws IOException {
        this.selector = Selector.open();
        return threadPool.initialize();
    }

    public void addChannelChange(ServerChannelChange channelChange){
        channelChanges.add(channelChange);
        selector.wakeup();
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            LoggingUtil.logError("Missing required arguments. Expecting " +
                    "\'java cs455.scaling.server.Server port-num thread-pool-size\'");
            System.exit(-1);
        }
        int port = Integer.parseInt(args[0]);
        int threadPoolSize = Integer.parseInt(args[1]);

        // Server instance
        Server server = new Server(port, threadPoolSize);
        boolean initialized = false;
        try {
            initialized = server.initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (initialized) {
            LoggingUtil.logInfo("Server started successfully!");
        }

        server.start();

    }

    private void start() {
        try {
            ServerSocketChannel ssc = ServerSocketChannel.open();

            ssc.configureBlocking(false);

            ServerSocket ss = ssc.socket();
            InetSocketAddress isa = new InetSocketAddress(port);
            ss.bind(isa);

            //Selector selector = Selector.open();

            ssc.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Listening on port " + port);
            JobQueue jobQueue = JobQueue.getInstance();

            while(true){
                // check for changes
                if(!channelChanges.isEmpty()){
                    System.out.println("Change size:" + channelChanges.size());
                    Iterator<ServerChannelChange> changes = channelChanges.iterator();
                    while (changes.hasNext()) {
                        ServerChannelChange channelChange = changes.next();
                        System.out.println("New Interest->" + channelChange.getNewInterest());
                        channelChange.getChannel().register(selector, channelChange.getNewInterest());
                        changes.remove();
                    }
                }

                // now check for new keys
                int num = selector.select();
                if (num == 0) {
                    continue;
                }

                Set keys = selector.selectedKeys();
                Iterator it = keys.iterator();

                while(it.hasNext()){
                    SelectionKey key = (SelectionKey) it.next();
                    it.remove();
                    if(!key.isValid()){
                        continue;
                    }
                    if (key.isAcceptable()) {
                        System.out.println("key" + key);
                        ConnectionAcceptTask connAcceptTask = new ConnectionAcceptTask(key, this);
                        jobQueue.addJob(connAcceptTask);
                    } else if (key.isReadable()) {
                        ReadTask readTask = new ReadTask(key, this);
                        jobQueue.addJob(readTask);
                    } else if (key.isWritable()){
                        WriteTask writeTask = new WriteTask(key, this);
                        jobQueue.addJob(writeTask);
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
