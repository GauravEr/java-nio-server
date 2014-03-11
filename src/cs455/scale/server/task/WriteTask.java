package cs455.scale.server.task;

import cs455.scale.server.ExtendedBuffer;
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

    private final ExtendedBuffer extendedBuffer;
    private final SocketChannel socketChannel;

    public WriteTask(SelectionKey key, Server server) {
        super(key, server);
        this.socketChannel = (SocketChannel)key.channel();
        //this.extendedBuffer = BufferManager.getInstance().getBuffer(socketChannel);
        this.extendedBuffer = (ExtendedBuffer) selectionKey.attachment();
    }

    @Override
    public void complete() {
        synchronized (extendedBuffer.writeLock) {
            if (extendedBuffer.isWritable()) {
                byte[] bytesToWrite = extendedBuffer.getFromWriteBacklog();
                ByteBuffer writeBuffer = ByteBuffer.wrap(bytesToWrite);
                // Check if the write buffer is empty which means we need to calculate the hash
                // otherwise it's half written buffer.
                if (writeBuffer.limit() == 20) {
                    Socket socket = socketChannel.socket();
                    LoggingUtil.logInfo(this.getClass(), "Sending hash " +
                            ScaleUtil.hexStringFromBytes(bytesToWrite) + "to client " +
                            socket.getInetAddress().getHostName() + ":" + socket.getPort());
                }
                try {
                    socketChannel.write(writeBuffer);
                    if(writeBuffer.hasRemaining()){
                        byte[] remaining = new byte[writeBuffer.remaining()];
                        writeBuffer.get(remaining);
                        extendedBuffer.completeWriting(remaining);
                    } else {
                        extendedBuffer.completeWriting();
                    }
                } catch (IOException e) {
                    try {
                        LoggingUtil.logError(this.getClass(), "Closing the client connection.");
                        socketChannel.close();
                        //BufferManager.getInstance().deregisterBuffer(socketChannel);
                        selectionKey.cancel();
                    } catch (IOException ignore) {

                    }
                }
            }
        }
    }
}
