package cs455.scale.server.task;

import cs455.scale.server.SocketChannelDataHolder;
import cs455.scale.server.Server;
import cs455.scale.util.LoggingUtil;
import cs455.scale.util.ScaleUtil;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Author: Thilina
 * Date: 3/2/14
 */
public class WriteTask extends AbstractTask{

    private final SocketChannelDataHolder socketChannelDataHolder;
    private final SocketChannel socketChannel;

    public WriteTask(SelectionKey key, Server server) {
        super(key, server);
        this.socketChannel = (SocketChannel)key.channel();
        this.socketChannelDataHolder = (SocketChannelDataHolder) selectionKey.attachment();
    }

    @Override
    public void complete() {
        synchronized (socketChannelDataHolder.writeLock) {
            if (socketChannelDataHolder.isWritable()) {
                byte[] bytesToWrite = socketChannelDataHolder.getFromWriteBacklog();
                if(bytesToWrite == null){
                    return;
                }
                ByteBuffer writeBuffer = ByteBuffer.wrap(bytesToWrite);
                // Check if the write buffer is empty which means we need to calculate the hash
                // otherwise it's a half written buffer.
                if (writeBuffer.limit() == 20) {
                    Socket socket = socketChannel.socket();
                    LoggingUtil.logInfo(this.getClass(), "Sending hash " +
                            ScaleUtil.hexStringFromBytes(bytesToWrite) + "to client " +
                            socket.getInetAddress().getHostName() + ":" + socket.getPort());
                }
                try {
                    // write the content
                    socketChannel.write(writeBuffer);
                    // if there is remaining content, add it back to the backlog.
                    if(writeBuffer.hasRemaining()){
                        byte[] remaining = new byte[writeBuffer.remaining()];
                        writeBuffer.get(remaining);
                        socketChannelDataHolder.completeWriting(remaining);
                    } else {    // complete write.
                        socketChannelDataHolder.completeWriting();
                    }
                } catch (IOException e) {
                    try {
                        LoggingUtil.logError(this.getClass(), "Closing the client connection.");
                        socketChannel.close();
                        selectionKey.cancel();
                    } catch (IOException ignore) {

                    }
                }
            }
        }
    }
}
