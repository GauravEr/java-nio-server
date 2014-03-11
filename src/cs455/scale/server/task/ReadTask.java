package cs455.scale.server.task;

import cs455.scale.server.*;
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
        ExtendedBuffer extendedBuffer = (ExtendedBuffer) selectionKey.attachment();
        ByteBuffer readBuffer = extendedBuffer.getReadBuffer();
        //synchronized (extendedBuffer) {
        try {
            synchronized (extendedBuffer.readLock) {
                int bytesRead = socketChannel.read(readBuffer);
                // client has terminated the connection
                if (bytesRead == -1) {
                    socketChannel.close();
                    //BufferManager.getInstance().deregisterBuffer(socketChannel);
                    selectionKey.cancel();
                    return;
                }

                if (!readBuffer.hasRemaining()) { // we have read 8k of data
                    readBuffer.flip();
                    Socket socket = socketChannel.socket();
                    LoggingUtil.logInfo(this.getClass(), "Received a hash from " + socket.getInetAddress().getHostName() +
                            ":" + socket.getPort());
                    byte[] receivedData = new byte[1024 * 8];
                    readBuffer.get(receivedData);
                    byte[] hashCodeInBytes = ScaleUtil.SHA1FromBytes(receivedData);
                    extendedBuffer.addToWriteBacklog(hashCodeInBytes);
                    readBuffer.clear();
                    selectionKey.interestOps(SelectionKey.OP_WRITE);
                    selectionKey.attach(extendedBuffer);
                }
            }
        } catch (IOException e) {
            try {
                if (selectionKey.isValid() && socketChannel.isOpen()) {
                    LoggingUtil.logError(this.getClass(), "Closing the client connection.");
                    socketChannel.close();
                    //BufferManager.getInstance().deregisterBuffer(socketChannel);
                    selectionKey.cancel();
                }
            } catch (IOException ignore) {

            }
        }
    }
}

