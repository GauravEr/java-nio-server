package cs455.scale.server;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * A data holder object attached with each channel.
 * It will contain the read buffer and a set of byte[] that needs to be written back.
 * The accessing entity needs to acquire corresponding read and write locks.
 * Author: Thilina
 * Date: 3/7/14
 */
public class SocketChannelDataHolder {

    private ByteBuffer readBuffer =  ByteBuffer.allocate(1024*8);
    public final Object readLock = new Object();
    public final Object writeLock = new Object();
    private List<byte[]> writeBacklog = new ArrayList<byte[]>();

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

    /**
     * This method is used when the writer cannot write all the data in the first byte[]
     * to the channel. So the remaining set of bytes needs to be added back to the head
     * of the list for next writer thread to precess.
     * @param remaining
     */
    public void completeWriting(byte[] remaining){
        synchronized (writeBacklog){
            writeBacklog.remove(0);
            writeBacklog.add(0, remaining);
        }
    }
}
