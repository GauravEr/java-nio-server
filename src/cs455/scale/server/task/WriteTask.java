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
                ByteBuffer writeBuffer = extendedBuffer.getWriteBuffer();
                // Check if the write buffer is empty which means we need to calculate the hash
                // otherwise it's half written buffer.
                if (writeBuffer.position() == 0) {
                    ByteBuffer readBuffer = extendedBuffer.getReadBuffer();
                    byte[] receivedData = new byte[1024*8];
                    readBuffer.get(receivedData);
                    byte[] hashCodeInBytes = ScaleUtil.SHA1FromBytes(receivedData);
                    Socket socket = socketChannel.socket();
                    LoggingUtil.logInfo(this.getClass(), "Sending hash " +
                            ScaleUtil.hexStringFromBytes(hashCodeInBytes) + "to client " +
                            socket.getInetAddress().getHostName() + ":" + socket.getPort());
                    writeBuffer.put(hashCodeInBytes);
                    writeBuffer.flip();
                    readBuffer.clear();
                }
                try {
                    socketChannel.write(writeBuffer);
                    if(writeBuffer.hasRemaining()){
                        JobQueue.getInstance().addJob(this);
                    } else {
                        writeBuffer.clear();
                        extendedBuffer.setReadable();
                        ServerChannelChange serverChannelChange =
                                new ServerChannelChange(socketChannel, SelectionKey.OP_READ);
                        server.addChannelChange(serverChannelChange);
                    }
                } catch (IOException e) {
                    try {
                        LoggingUtil.logError(this.getClass(), "Closing the client connection.");
                        socketChannel.close();
                        BufferManager.getInstance().deregisterBuffer(socketChannel);
                        selectionKey.cancel();
                    } catch (IOException ignore) {

                    }
                }
            }
        }
    }
}
