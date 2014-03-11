package cs455.scale.server;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Thilina
 * Date: 3/7/14
 */
public class ExtendedBuffer {

    private ByteBuffer readBuffer =  ByteBuffer.allocate(1024*8);
    //private ByteBuffer writeBuffer = ByteBuffer.allocate(20);
    public Object readLock = new Object();
    public Object writeLock = new Object();
    private List<byte[]> writeBacklog = new ArrayList<byte[]>();

    //private volatile AtomicBoolean readReady = new AtomicBoolean(true);

    public ByteBuffer getReadBuffer(){
        return readBuffer;
    }

    public void addToWriteBacklog(byte[] tobeWritten){
        synchronized (writeBacklog){
            writeBacklog.add(tobeWritten);
        }
    }

    public byte[] getFromWriteBacklog(){
        synchronized (writeBacklog){
            if(writeBacklog.size() > 0){
                return writeBacklog.get(0);
            } else {
                return null;
            }
        }
    }

    public boolean isWritable(){
        synchronized (writeBacklog){
            return !writeBacklog.isEmpty();
        }
    }

    public void completeWriting(){
        synchronized (writeBacklog){
            writeBacklog.remove(0);
        }
    }

    public void completeWriting(byte[] remaining){
        synchronized (writeBacklog){
            writeBacklog.remove(0);
            writeBacklog.add(0, remaining);
        }
    }
}
