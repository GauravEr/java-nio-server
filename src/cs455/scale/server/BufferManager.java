package cs455.scale.server;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: Thilina
 * Date: 3/2/14
 */
public class BufferManager {

    private static final BufferManager instance = new BufferManager();

    private Map<SocketChannel, ExtendedBuffer> bufferMap = new ConcurrentHashMap<SocketChannel, ExtendedBuffer>();

    private BufferManager(){

    }

    public static BufferManager getInstance(){
        return instance;
    }

    public synchronized ExtendedBuffer getBuffer(SocketChannel socketChannel){
        ExtendedBuffer extendedBuffer;
        if(bufferMap.containsKey(socketChannel)){
            extendedBuffer = bufferMap.get(socketChannel);
        } else {
            extendedBuffer = new ExtendedBuffer();
            bufferMap.put(socketChannel, extendedBuffer);
        }
        return extendedBuffer;
    }
    public synchronized void deregisterBuffer(SocketChannel socketChannel){
        bufferMap.remove(socketChannel);
    }
}
