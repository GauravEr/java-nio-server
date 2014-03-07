package cs455.scale.server.task;

import cs455.scale.server.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Author: Thilina
 * Date: 3/2/14
 */
public class WriteTask extends AbstractTask{

    private final ExtendedBuffer extendedBuffer;
    private final SocketChannel socketChannel;

    public WriteTask(SelectionKey key, Server server) {
        super(key, server);
        this.socketChannel = (SocketChannel)key.channel();
        this.extendedBuffer = BufferManager.getInstance().getBuffer(socketChannel);
    }

    @Override
    public void complete() {
        synchronized (extendedBuffer) {
            if (extendedBuffer.isWritable()) {
                ByteBuffer buffer = extendedBuffer.getByteBuffer();
                try {
                    socketChannel.write(buffer);
                    if(buffer.hasRemaining()){
                        JobQueue.getInstance().addJob(this);
                    } else {
                        buffer.clear();
                        extendedBuffer.setReadable();
                        ServerChannelChange serverChannelChange =
                                new ServerChannelChange(socketChannel, SelectionKey.OP_READ);
                        server.addChannelChange(serverChannelChange);
                    }
                } catch (IOException e) {
                    try {
                        System.out.println("[" + jobId + "]Cancelling Write key.");
                        socketChannel.close();
                        selectionKey.cancel();
                    } catch (IOException e1) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
