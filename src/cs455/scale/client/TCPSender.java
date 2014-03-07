package cs455.scale.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Author: Thilina
 * Date: 3/2/14
 */
public class TCPSender {
    private DataOutputStream dataOutputStream;

    public TCPSender(Socket socket) throws IOException {
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }

    public synchronized void sendData(byte[] data) throws IOException {
        dataOutputStream.write(data);
        dataOutputStream.flush();
    }
}
