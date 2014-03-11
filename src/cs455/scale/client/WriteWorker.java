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
public class WriteWorker extends Thread {

    private final int sleepInterval;
    private final SocketChannel socketChannel;
    private ByteBuffer byteBuffer;
    private final Client client;

    public WriteWorker(int sleepInterval, SocketChannel channel, Client client) {
        this.sleepInterval = sleepInterval;
        this.socketChannel = channel;
        this.client = client;
        byteBuffer = ByteBuffer.allocate(1024 * 8);
    }

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                try {
                    wait();
                    if (byteBuffer.position() == 0) {
                        final byte[] payload = ScaleUtil.getPayLoad();
                        final String hashCode = ScaleUtil.hexStringFromBytes(ScaleUtil.SHA1FromBytes(payload));
                        client.addHashCode(hashCode);
                        LoggingUtil.logInfo(this.getClass(), "Sending hash code: " + hashCode);
                        byteBuffer.put(payload);
                        byteBuffer.flip();
                    }
                    socketChannel.write(byteBuffer);
                    if (!byteBuffer.hasRemaining()) {
                        byteBuffer.clear();
                    }
                    // ensure the message rate
                    Thread.sleep(sleepInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    try {
                        socketChannel.close();
                        client.cancelChannel(socketChannel);
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
