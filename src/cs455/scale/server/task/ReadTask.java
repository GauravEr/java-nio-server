package cs455.scale.server.task;

import cs455.scale.server.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Author: Thilina
 * Date: 3/1/14
 */
public class ReadTask extends AbstractTask {

    private final SelectionKey selectionKey;
    private final Server server;

    public ReadTask(SelectionKey selectionKey, Server server) {
        super(selectionKey, server);
        this.selectionKey = selectionKey;
        this.server = server;
    }

    @Override
    public void complete() {
        System.out.println(jobId + "->" + this.getClass());
        System.out.println("Reading Started!");
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        BufferManager bufferManager = BufferManager.getInstance();
        ExtendedBuffer extendedBuffer = bufferManager.getBuffer(socketChannel);
        ByteBuffer byteBuffer = extendedBuffer.getReadBuffer();
        synchronized (extendedBuffer) {
            if (extendedBuffer.isReadable()) {
                try {
                    int bytesRead = socketChannel.read(byteBuffer);
                    if (bytesRead == -1) {
                        socketChannel.close();
                        BufferManager.getInstance().deregisterBuffer(socketChannel);
                        selectionKey.cancel();
                        return;
                    }

                    if (!byteBuffer.hasRemaining()) { // we have read 8k of data
                        byteBuffer.flip();
                        System.out.println("Completed reading on message!");
                        extendedBuffer.setWritable();
//                        ServerChannelChange serverChannelChange =
//                                new ServerChannelChange(socketChannel, SelectionKey.OP_WRITE);
//                        server.addChannelChange(serverChannelChange);
                    }
                } catch (IOException e) {
                    try {
                        e.printStackTrace();
                        System.out.println("Cancelling Read key.");
                        socketChannel.close();
                        BufferManager.getInstance().deregisterBuffer(socketChannel);
                        selectionKey.cancel();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            else {
                JobQueue.getInstance().addJob(this);
            }
        }
    }
}
