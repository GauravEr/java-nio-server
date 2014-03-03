package cs455.scale.server.task;

import cs455.scale.server.JobQueue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Author: Thilina
 * Date: 3/2/14
 */
public class WriteTask implements Task{

    private final ByteBuffer buffer;
    private final SocketChannel socketChannel;
    private final SelectionKey selectionKey;

    public WriteTask(ByteBuffer buffer, SocketChannel socketChannel, SelectionKey key) {
        this.socketChannel = socketChannel;
        this.buffer = buffer;
        this.selectionKey = key;
    }

    @Override
    public void complete() {
        // need to implement hashing here
        try {
            System.out.println("Writing Started!");
            socketChannel.write(buffer);
            if(buffer.hasRemaining()){
                JobQueue.getInstance().addJob(this);
            } else {
                selectionKey.cancel();
                //buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
