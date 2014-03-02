package cs455.scale.server.task;

import cs455.scale.util.LoggingUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Author: Thilina
 * Date: 3/1/14
 */
public class ConnectionAcceptTask implements Task {

    private final ServerSocket serverSocket;
    private final Selector selector;

    public ConnectionAcceptTask(Selector selector, ServerSocket serverSocket) {
        this.selector = selector;
        this.serverSocket = serverSocket;
    }

    @Override
    public void complete() {
        try {
            Socket socket = serverSocket.accept();
            SocketChannel socketChannel = socket.getChannel();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            LoggingUtil.logError("Error accepting connection.", e);
        }
    }
}
