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
        System.out.println("Reading Started!");
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        BufferManager bufferManager = BufferManager.getInstance();
        JobQueue jobQueue = JobQueue.getInstance();
        try {
            SocketAddress socketAddress = socketChannel.getRemoteAddress();
            ByteBuffer byteBuffer = bufferManager.getBuffer(socketAddress);
            socketChannel.read(byteBuffer);
            if(!byteBuffer.hasRemaining()){ // we have read 8k of data
                byteBuffer.flip();
                //
                //byte[] msg = new byte[8*1024];
//                System.out.println(">>>>>>");
//                byteBuffer.get(msg);
//                System.out.println("<<<<<<");
//                System.out.println("Server Received : " + Arrays.toString(msg));
//                byteBuffer.flip();
                //
                WriteTask writeTask = new WriteTask(byteBuffer, socketChannel, selectionKey);
                jobQueue.addJob(writeTask);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
