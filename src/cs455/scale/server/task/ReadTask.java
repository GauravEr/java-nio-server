package cs455.scale.server.task;

import cs455.scale.server.*;
import cs455.scale.util.LoggingUtil;

import java.io.IOException;
import java.net.Socket;
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
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        BufferManager bufferManager = BufferManager.getInstance();
        ExtendedBuffer extendedBuffer = bufferManager.getBuffer(socketChannel);
        ByteBuffer byteBuffer = extendedBuffer.getReadBuffer();
        synchronized (extendedBuffer) {
            if (extendedBuffer.isReadable()) {
                try {
                    int bytesRead = socketChannel.read(byteBuffer);
                    // client has terminated the connection
                    if (bytesRead == -1) {
                        socketChannel.close();
                        BufferManager.getInstance().deregisterBuffer(socketChannel);
                        selectionKey.cancel();
                        return;
                    }

                    if (!byteBuffer.hasRemaining()) { // we have read 8k of data
                        byteBuffer.flip();
                        Socket socket = socketChannel.socket();
                        LoggingUtil.logInfo(this.getClass(), "Received a hash from " + socket.getInetAddress().getHostName() +
                                ":" + socket.getPort());
                        extendedBuffer.setWritable();

                        ServerChannelChange serverChannelChange =
                                new ServerChannelChange(socketChannel, SelectionKey.OP_WRITE);
                        server.addChannelChange(serverChannelChange);
                    }
                } catch (IOException e) {
                    try {
                        if (selectionKey.isValid() && socketChannel.isOpen()) {
                            LoggingUtil.logError(this.getClass(), "Closing the client connection.");
                            socketChannel.close();
                            BufferManager.getInstance().deregisterBuffer(socketChannel);
                            selectionKey.cancel();
                        }
                    } catch (IOException ignore) {

                    }
                }
            }
            else { // defer the read
                JobQueue.getInstance().addJob(this);
            }
        }
    }
}
