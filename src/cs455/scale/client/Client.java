package cs455.scale.client;

import cs455.scale.client.blocking.BlockingClient;
import cs455.scale.util.LoggingUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Author: Thilina
 * Date: 3/8/14
 */
public class Client {

    private Selector selector;
    private final String serverHost;
    private final int serverPort;
    private final int messageRate;
    private SocketChannel socketChannel;
    private WriteWorker writeWorker;
    private ReadWorker readWorker;
    private List<String> hashCodes = new LinkedList<String>();
    private final Queue<SocketChannel> cancelledChannels = new ConcurrentLinkedQueue<SocketChannel>();

    public Client(String serverHost, int serverPort, int messageRate) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.messageRate = messageRate;
    }


    public boolean initialize() {
        try {
            selector = Selector.open();
        } catch (IOException e) {
            LoggingUtil.logError(this.getClass(), "Error opening the selector.", e);
            return false;
        }
        try {
            // create the socket channel.
            socketChannel = SocketChannel.open();
            // configure it to be non-blocking
            socketChannel.configureBlocking(false);
            SocketAddress socketAddress = new InetSocketAddress(serverHost, serverPort);
            socketChannel.connect(socketAddress);
            LoggingUtil.logInfo(this.getClass(), "Successfully connected to " + socketAddress);

            // start the write worker thread.
            writeWorker = new WriteWorker(5000/messageRate, socketChannel, this);
            writeWorker.start();

            // start the read worker
            readWorker = new ReadWorker(socketChannel, this);
            readWorker.start();
        } catch (IOException e) {
            LoggingUtil.logError(this.getClass(), "Error initializing the socket channel.", e);
            return false;
        }
        return true;
    }

    public void start() {
        try {
            // register for connect.
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            while (true) {
                // first check for cancelled channels.
                for(SocketChannel channel: cancelledChannels){
                    System.out.println("Cancelling a Channel");
                    SelectionKey selectionKey = channel.keyFor(selector);
                    selectionKey.cancel();
                }

                // now check for new keys
                int numOfKeys = selector.select();
                // no new selected keys. start the loop again.
                if (numOfKeys == 0) {
                    continue;
                }

                // get the keys
                Set keys = selector.selectedKeys();
                Iterator it = keys.iterator();

                while (it.hasNext()) {
                    SelectionKey key = (SelectionKey) it.next();
                    it.remove();

                    if(key.isValid() && key.isConnectable()){
                        handleConnect(key);
                    } else if(key.isValid() && key.isReadable()){
                        readWorker.wakeUp();
                    } else if(key.isValid() && key.isWritable()){
                        writeWorker.wakeUp();
                    }
                }
            }
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleConnect(SelectionKey key){
        SocketChannel channel = (SocketChannel) key.channel();
        if(!channel.isConnected()){
            try {
                channel.finishConnect();
                SelectionKey readKey = channel.register(selector, SelectionKey.OP_WRITE);
                channel.register(selector, readKey.interestOps() | SelectionKey.OP_READ);
            } catch (IOException e) {
                LoggingUtil.logError(this.getClass(), "Error when completing connection.", e);
                try {
                    channel.close();
                    key.cancel();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        // Check if the required arguments are provided.
        if (args.length < 3) {
            if (args.length < 3) {
                LoggingUtil.logError(BlockingClient.class, "Required arguments are missing. Expected format " +
                        "\'java cs455.scaling.client.BlockingClient server-host server-port message-rate\' \n");
                System.exit(-1);
            }
        }

        // parse the input arguments.
        String serverHost = args[0];
        int port = Integer.parseInt(args[1]);
        int messageRate = Integer.parseInt(args[2]);

        // Create the client instance and initialize
        Client client = new Client(serverHost, port, messageRate);
        boolean initStatus = client.initialize();

        // Check the initialization status.
        if (!initStatus) {
            LoggingUtil.logError(client.getClass(), "Client initialization Failed!");
            System.exit(-1);
        }

        client.start();

    }

    public void addHashCode(String hashCode){
        synchronized (hashCodes){
            hashCodes.add(hashCode);
            System.out.println("hash elements: " + hashCodes.size());
        }
    }

    public boolean checkHashCode(String hashCode){
        synchronized (hashCodes){
            Iterator<String> hashValues = hashCodes.iterator();
            while (hashValues.hasNext()){
                String storedHash = hashValues.next();
                if(storedHash.equals(hashCode)){
                    hashValues.remove();
                    return true;
                }
            }
        }
        return false;
    }

    public void cancelChannel(SocketChannel socketChannel){
        cancelledChannels.add(socketChannel);
    }

}
