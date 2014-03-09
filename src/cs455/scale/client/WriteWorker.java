package cs455.scale.client;

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

    public WriteWorker(int sleepInterval, SocketChannel channel) {
        this.sleepInterval = sleepInterval;
        this.socketChannel = channel;
        byteBuffer = ByteBuffer.allocate(1024 * 8);
    }

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                try {
                    wait();
                    if (byteBuffer.position() == 0) {
                        byte[] payload = ScaleUtil.getPayLoad();

                        System.out.println("--> Sent 10 bytes");
                        for(int i = 0; i < 10; i++){
                            System.out.print(payload[i] + ", ");
                        }
                        System.out.println("---------------");

                        byteBuffer.put(payload);
                        byteBuffer.flip();
                    }
                    System.out.println("Position:" + byteBuffer.position() + ", Limit:" + byteBuffer.limit());
                    int count = socketChannel.write(byteBuffer);
                    if (!byteBuffer.hasRemaining()) {
                        byteBuffer.clear();
                    }
                    System.out.println("No. of bytes written: " + count);
                    Thread.sleep(sleepInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized void wakeUp(){
        this.notify();
    }
}
