package cs455.scale.client;

import cs455.scale.util.LoggingUtil;
import cs455.scale.util.ScaleUtil;

import java.io.IOException;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Author: Thilina
 * Date: 3/2/14
 */
public class Client {

    private String serverHost;
    private int serverPort;
    private int messageRate;
    private Socket socket = null;
    private TCPSender tcpSender = null;
    private TCPReceiver tcpReceiver = null;
    private Queue<byte[]> sentData = new ConcurrentLinkedDeque<byte[]>();

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
            public void invoke(byte[] data) {
                byte[] sent = sentData.remove();
                System.out.println("Received data:");
                for (int i = 0; i < sent.length; i++) {
                    System.out.print(data[i] + ",");

                }
                System.out.println();
                //System.out.println("data is equal!");
            }
        });
        tcpReceiver.start();
    }

    public void sendMessage() throws IOException {
        byte[] payload = ScaleUtil.getPayLoad();
        //System.out.println("Sent: " + Arrays.toString(payload));
        System.out.println("last bytes----------------------------------");
        for(int i = 0; i < payload.length; i++){
            System.out.print(payload[i] + ",");
        }
        System.out.println("---------------------------------------------");
        tcpSender.sendData(payload);
        sentData.add(payload);
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            LoggingUtil.logError("Required arguments are missing. Expected format " +
                    "\'java cs455.scaling.client.Client server-host server-port message-rate\' \n");
        }

        String hostName = args[0];
        int port = Integer.parseInt(args[1]);
        int msgRate = Integer.parseInt(args[2]);

        try {
            Client client = new Client(hostName, port, msgRate);
            client.init();
            client.sendMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
