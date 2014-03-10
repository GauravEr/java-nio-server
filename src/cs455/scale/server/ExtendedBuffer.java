package cs455.scale.server;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Author: Thilina
 * Date: 3/7/14
 */
public class ExtendedBuffer {

    private ByteBuffer readBuffer =  ByteBuffer.allocate(1024*8);
    private ByteBuffer writeBuffer = ByteBuffer.allocate(20);
    private volatile AtomicBoolean readReady = new AtomicBoolean(true);

    public ByteBuffer getReadBuffer(){
        return readBuffer;
    }

    public void setWritable(){
        readReady.set(false);
        System.out.println("Readable:" + readReady.get());
    }

    public void setReadable(){
        readReady.set(true);
        System.out.println("Readable:" + readReady.get());
    }

    public boolean isReadable(){
        return readReady.get();
    }

    public boolean isWritable(){
        return !isReadable();
    }

    public ByteBuffer getWriteBuffer() {
        return writeBuffer;
    }
}
