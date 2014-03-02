package cs455.scale.server.task;

import cs455.scale.server.BufferManager;
import cs455.scale.server.JobQueue;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Author: Thilina
 * Date: 3/1/14
 */
public class ReadTask implements Task {

    private final SelectionKey selectionKey;

    public ReadTask(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

    @Override
    public void complete() {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        BufferManager bufferManager = BufferManager.getInstance();
        JobQueue jobQueue = JobQueue.getInstance();
        try {
            SocketAddress socketAddress = socketChannel.getRemoteAddress();
            ByteBuffer byteBuffer = bufferManager.getBuffer(socketAddress);
            socketChannel.read(byteBuffer);
            if(!byteBuffer.hasRemaining()){ // we have read 8k of data
                byteBuffer.flip();
                WriteTask writeTask = new WriteTask(byteBuffer, socketChannel);
                jobQueue.addJob(writeTask);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
