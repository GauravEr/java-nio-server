package cs455.scale.server.task;

import cs455.scale.server.BufferManager;
import cs455.scale.server.JobQueue;
import cs455.scale.server.Server;
import cs455.scale.server.ServerChannelChange;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Author: Thilina
 * Date: 3/2/14
 */
public class WriteTask extends AbstractTask{

    private final ByteBuffer buffer;
    private final SocketChannel socketChannel;

    public WriteTask(SelectionKey key, Server server) {
        super(key, server);
        this.socketChannel = (SocketChannel)key.channel();
        this.buffer = BufferManager.getInstance().getBuffer(socketChannel);
    }

    @Override
    public void complete() {
        System.out.println(jobId + "->" + this.getClass());
        // need to implement hashing here
        try {
            System.out.println("Writing Started!");
            System.out.println("To Be Written: " + buffer.position());
            socketChannel.write(buffer);
            if(buffer.hasRemaining()){
                JobQueue.getInstance().addJob(this);

            } else {
                System.out.println("Writing Completed!");
                BufferManager.getInstance().deregisterBuffer(socketChannel);
                ServerChannelChange serverChannelChange =
                        new ServerChannelChange(socketChannel, SelectionKey.OP_READ);
                server.addChannelChange(serverChannelChange);
                //buffer.clear();
            }
        } catch (IOException e) {
            try {
                System.out.println("Cancelling Write key.");
                socketChannel.close();
                selectionKey.cancel();
            } catch (IOException e1) {
                e.printStackTrace();
            }
        }
    }
}
