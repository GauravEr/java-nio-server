package cs455.scale.server;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: Thilina
 * Date: 3/2/14
 */
public class BufferManager {

    private static final BufferManager instance = new BufferManager();

    private Map<SocketAddress, ByteBuffer> bufferMap = new ConcurrentHashMap<SocketAddress, ByteBuffer>();

    private BufferManager(){

    }

    public static BufferManager getInstance(){
        return instance;
    }

    public void registerBuffer(SocketAddress socketAddress, ByteBuffer buffer){
        bufferMap.put(socketAddress, buffer);
    }

    public ByteBuffer getBuffer(SocketAddress socketAddress){
        ByteBuffer byteBuffer = ByteBuffer.allocate(8*1024);
        if(bufferMap.containsKey(socketAddress)){
            byteBuffer = bufferMap.get(socketAddress);
        } else {
            bufferMap.put(socketAddress, byteBuffer);
        }
        return byteBuffer;
    }
}
