package cs455.scale.server.task;

import cs455.scale.server.BufferManager;
import cs455.scale.server.Server;
import cs455.scale.server.ServerChannelChange;

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
        super(selectionKey,server);
        this.selectionKey = selectionKey;
        this.server = server;
    }

    @Override
    public void complete() {
        System.out.println(jobId + "->" + this.getClass());
        System.out.println("Reading Started!");
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        BufferManager bufferManager = BufferManager.getInstance();
        try {
            ByteBuffer byteBuffer = bufferManager.getBuffer(socketChannel);
            socketChannel.read(byteBuffer);
            if(!byteBuffer.hasRemaining()){ // we have read 8k of data
                byteBuffer.flip();
                ServerChannelChange serverChannelChange =
                        new ServerChannelChange(socketChannel, SelectionKey.OP_WRITE);
                server.addChannelChange(serverChannelChange);
            }
        } catch (IOException e) {
            try {
                System.out.println("Cancelling Read key.");
                socketChannel.close();
                selectionKey.cancel();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
