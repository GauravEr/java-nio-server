package cs455.scale.server;

import java.nio.channels.SocketChannel;

/**
 * Author: Thilina
 * Date: 3/3/14
 */
public class ServerChannelChange {
    private final SocketChannel channel;
    private final int newInterest;
    private final Object attachment;

    public ServerChannelChange(SocketChannel channel, int newInterest, Object attachment) {
        this.channel = channel;
        this.newInterest = newInterest;
        this.attachment = attachment;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public int getNewInterest() {
        return newInterest;
    }

    public Object getAttachment() {
        return attachment;
    }
}
