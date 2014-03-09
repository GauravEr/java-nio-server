package cs455.scale.client;

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

    public ReadWorker(SocketChannel socketChannel) {
        buffer = ByteBuffer.allocate(1024*8);
        this.socketChannel = socketChannel;
    }

    @Override
    public void run() {
        while (true){
            synchronized (this){
                try {
                    wait();
                    int count = socketChannel.read(buffer);
                    if(!buffer.hasRemaining()){
                        buffer.flip();
                        System.out.println("Received complete message!");
                        byte[] receivedData = new byte[1024*8];
                        buffer.get(receivedData);
                        System.out.println("<----- Received 10 bytes");
                        for(int i = 0; i < 10; i++){
                            System.out.print(receivedData[i] + ", ");
                        }
                        System.out.println("---------------");
                        buffer.clear();
                    }
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
