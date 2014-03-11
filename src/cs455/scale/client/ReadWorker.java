package cs455.scale.client;

import cs455.scale.util.LoggingUtil;
import cs455.scale.util.ScaleUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Author: Thilina
 * Date: 3/8/14
 */
public class ReadWorker extends Thread {

    private final ByteBuffer buffer;
    private final SocketChannel socketChannel;
    private final Client client;

    public ReadWorker(SocketChannel socketChannel, Client client) {
        buffer = ByteBuffer.allocate(20);   // SHA1 hash values are 160 bits
        this.socketChannel = socketChannel;
        this.client = client;
    }

    @Override
    public void run() {
        while (true){
            synchronized (this){
                try {
                    wait();
                    int count = socketChannel.read(buffer);
                    if(count == -1){
                        socketChannel.close();
                        break;
                    }

                    if(!buffer.hasRemaining()){
                        buffer.flip();
                        byte[] receivedData = new byte[20];
                        buffer.get(receivedData);
                        String hashString = ScaleUtil.hexStringFromBytes(receivedData);
                        if(!client.checkHashCode(hashString)){
                            LoggingUtil.logError(this.getClass(), "Invalid Hash code: " + hashString);
                        }
                        buffer.clear();
                    }
                } catch (InterruptedException e) {
                    // thread terminating
                } catch (IOException e) {
                    try {
                        LoggingUtil.logError(this.getClass(), "Error reading from socket channel, closing the channel.");
                        socketChannel.close();
                    } catch (IOException ignore) {

                    }
                }

            }
        }
    }

    public synchronized void wakeUp(){
        this.notify();
    }
}
