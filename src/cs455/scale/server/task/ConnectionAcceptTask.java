package cs455.scale.server.task;

import cs455.scale.server.Server;
import cs455.scale.util.LoggingUtil;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Author: Thilina
 * Date: 3/1/14
 */
public class ConnectionAcceptTask extends AbstractTask {

    public ConnectionAcceptTask(SelectionKey selectionKey, Server server) {
        super(selectionKey, server);
    }

    @Override
    public void complete() {
        System.out.println(jobId + "->" + this.getClass());
        try {
            SocketChannel socketChannel = ((ServerSocketChannel)selectionKey.channel()).accept();
            if (socketChannel != null) {
                socketChannel.configureBlocking(false);
                /*ServerChannelChange serverChannelChange = new ServerChannelChange(
                        socketChannel, SelectionKey.OP_READ);
                server.addChannelChange(serverChannelChange);*/
                System.out.println("Connection Accept Completed!");
            }
        } catch (IOException e) {
            LoggingUtil.logError(this.getClass(), "Error accepting connection.", e);
        }
    }
}
