package cs455.scale.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

/**
 * Author: Thilina
 * Date: 3/2/14
 */
public class TCPReceiver extends Thread {
    protected Socket socket;
    protected DataInputStream dataInputStream;

    public TCPReceiver(Socket socket) throws IOException {
        this.socket = socket;
        dataInputStream = new DataInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        while(socket != null){
            try {
                int dataLength = 8 * 1024;
                if (dataLength > 0) {
                    byte[] data = new byte[dataLength];
                    dataInputStream.readFully(data, 0, dataLength);
                    System.out.println("Received: " + Arrays.toString(data));
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
