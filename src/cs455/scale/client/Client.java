package cs455.scale.client;

import cs455.scale.util.LoggingUtil;
import cs455.scale.util.ScaleUtil;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: Thilina
 * Date: 3/2/14
 */
public class Client implements Runnable{

    private String serverHost;
    private int serverPort;
    private int messageRate;
    private Socket socket = null;
    private TCPSender tcpSender = null;
    private TCPReceiver tcpReceiver = null;
    private Map<String, byte[]> sentData = new ConcurrentHashMap<String, byte[]>();
    private AtomicInteger sentCount = new AtomicInteger(0);
    private AtomicInteger receivedCount = new AtomicInteger(0);

    public Client(String serverHost, int serverPort, int messageRate) throws IOException {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.messageRate = messageRate;
        socket = new Socket(serverHost, serverPort, ScaleUtil.getHostInetAddress(), 0);
        tcpSender = new TCPSender(socket);
    }

    public void init() throws IOException {
        tcpReceiver = new TCPReceiver(socket, new Callback() {
            @Override
            public void invoke(byte[] receivedBytes) {

               // System.out.println("Received data:");
                /*for (int i = 0; i < data.length; i++) {
                    System.out.print(data[i] + ",");

                }*/
                String key = new String(receivedBytes, 0, 5);
                byte[] sentBytes = sentData.get(key);
                if(!Arrays.equals(receivedBytes, sentBytes)){
                    System.out.println("Data Corruption!");
                } else {
                    receivedCount.getAndIncrement();
                    System.out.println("[" + Thread.currentThread().getId() + "] " + "Sent: " + sentCount.get()
                    + ", Received: " + receivedCount.get());
                }
            }
        });
        tcpReceiver.start();
    }

    public void sendMessage() throws IOException {
        byte[] payload = ScaleUtil.getPayLoad();
        //System.out.println("Sent: " + Arrays.toString(payload));
//        System.out.println("sent data ----------------------------------");
//        for(int i = 0; i < payload.length; i++){
//            System.out.print(payload[i] + ",");
//        }
//        System.out.println("---------------------------------------------");
        String key = new String(payload, 0, 5);
        tcpSender.sendData(payload);
        sentData.put(key, payload);
        sentCount.getAndIncrement();
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            LoggingUtil.logError(Client.class, "Required arguments are missing. Expected format " +
                    "\'java cs455.scaling.client.Client server-host server-port message-rate\' \n");
        }

        String hostName = args[0];
        int port = Integer.parseInt(args[1]);
        int msgRate = Integer.parseInt(args[2]);

        for(int i = 0; i < 100; i++){
        try {
            Client client = new Client(hostName, port, msgRate);
            client.init();
            Thread t = new Thread(client);
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        }

    }

    @Override
    public void run() {
        for(int i = 0; i < 10; i ++){
            try {
                sendMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
