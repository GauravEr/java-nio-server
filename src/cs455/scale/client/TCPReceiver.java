package cs455.scale.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Author: Thilina
 * Date: 3/2/14
 */
public class TCPReceiver extends Thread {
    protected Socket socket;
    protected DataInputStream dataInputStream;
    private Callback callback;

    public TCPReceiver(Socket socket, Callback callback) throws IOException {
        this.socket = socket;
        dataInputStream = new DataInputStream(socket.getInputStream());
        this.callback = callback;
    }

    @Override
    public void run() {
        while(socket != null){
            try {
                int dataLength = 10;
                if (dataLength > 0) {
                    byte[] data = new byte[dataLength];
                    dataInputStream.readFully(data);
                    //System.out.println("Received: " + Arrays.toString(data));
                    callback.invoke(data);
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
