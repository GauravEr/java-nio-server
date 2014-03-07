package cs455.scale.server;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: Thilina
 * Date: 3/2/14
 */
public class BufferManager {

    private static final BufferManager instance = new BufferManager();

    private Map<SocketChannel, ByteBuffer> bufferMap = new ConcurrentHashMap<SocketChannel, ByteBuffer>();

    private BufferManager(){

    }

    public static BufferManager getInstance(){
        return instance;
    }

    public void deregisterBuffer(SocketChannel socketChannel){
        bufferMap.remove(socketChannel);
    }

    public ByteBuffer getBuffer(SocketChannel socketChannel){
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024*8);
        if(bufferMap.containsKey(socketChannel)){
            byteBuffer = bufferMap.get(socketChannel);
        } else {
            bufferMap.put(socketChannel, byteBuffer);
        }
        return byteBuffer;
    }
}
