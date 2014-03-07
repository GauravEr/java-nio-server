package cs455.scale.server;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Author: Thilina
 * Date: 3/7/14
 */
public class ExtendedBuffer {

    private ByteBuffer byteBuffer =  ByteBuffer.allocate(1024*8);
    private volatile AtomicBoolean readReady = new AtomicBoolean(true);

    public ByteBuffer getByteBuffer(){
        return byteBuffer;
    }

    public void setWritable(){
        readReady.set(false);
    }

    public void setReadable(){
        readReady.set(true);
    }

    public boolean isReadable(){
        return readReady.get();
    }

    public boolean isWritable(){
        return !isReadable();
    }

}
