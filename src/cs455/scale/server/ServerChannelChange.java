package cs455.scale.server;

import java.nio.channels.SocketChannel;

/**
 * Author: Thilina
 * Date: 3/3/14
 */
public class ServerChannelChange {
    private final SocketChannel channel;
    private final int newInterest;

    public ServerChannelChange(SocketChannel channel, int newInterest) {
        this.channel = channel;
        this.newInterest = newInterest;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public int getNewInterest() {
        return newInterest;
    }
}
