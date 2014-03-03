package cs455.scale.client;

import cs455.scale.util.LoggingUtil;
import cs455.scale.util.ScaleUtil;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

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

    public Client(String serverHost, int serverPort, int messageRate) throws IOException {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.messageRate = messageRate;
        socket = new Socket(serverHost, serverPort, ScaleUtil.getHostInetAddress(), 0);
        tcpSender = new TCPSender(socket);
        tcpReceiver = new TCPReceiver(socket);
        tcpReceiver.start();
    }

    public void sendMessage() throws IOException {
        byte[] payload = ScaleUtil.getPayLoad();
        System.out.println("Sent: " + Arrays.toString(payload));
        tcpSender.sendData(payload);
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
            client.sendMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
