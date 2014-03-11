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
        SocketChannelDataHolder socketChannelDataHolder = (SocketChannelDataHolder) selectionKey.attachment();
        ByteBuffer readBuffer = socketChannelDataHolder.getReadBuffer();
        try {
            synchronized (socketChannelDataHolder.readLock) {
                int bytesRead = socketChannel.read(readBuffer);
                // client has terminated the connection
                if (bytesRead == -1) {
                    socketChannel.close();
                    selectionKey.cancel();
                    return;
                }

                if (!readBuffer.hasRemaining()) { // we have read 8k of data
                    readBuffer.flip();
                    Socket socket = socketChannel.socket();
                    LoggingUtil.logInfo(this.getClass(), "Received a message from " + socket.getInetAddress().getHostName() +
                            ":" + socket.getPort());
                    byte[] receivedData = new byte[1024 * 8];
                    readBuffer.get(receivedData);
                    // calculate the hash and add it to the write backlog.
                    byte[] hashCodeInBytes = ScaleUtil.SHA1FromBytes(receivedData);
                    socketChannelDataHolder.addToWriteBacklog(hashCodeInBytes);
                    // reset the read buffer and prepare for the next read.
                    readBuffer.clear();
                }
            }
        } catch (IOException e) {
            try {
                if (selectionKey.isValid() && socketChannel.isOpen()) {
                    LoggingUtil.logError(this.getClass(), "Closing the client connection.");
                    socketChannel.close();
                    selectionKey.cancel();
                }
            } catch (IOException ignore) {

            }
        }
    }
}

