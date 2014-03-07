package cs455.scale.server.task;

import cs455.scale.server.BufferManager;
import cs455.scale.server.Server;
import cs455.scale.server.ServerChannelChange;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Author: Thilina
 * Date: 3/1/14
 */
public class ReadTask extends AbstractTask {

    private final SelectionKey selectionKey;
    private final Server server;

    public ReadTask(SelectionKey selectionKey, Server server) {
        super(selectionKey,server);
        this.selectionKey = selectionKey;
        this.server = server;
    }

    @Override
    public void complete() {
        System.out.println(jobId + "->" + this.getClass());
        System.out.println("Reading Started!");
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        BufferManager bufferManager = BufferManager.getInstance();
        try {
            ByteBuffer byteBuffer = bufferManager.getBuffer(socketChannel);



            int bytesRead = socketChannel.read(byteBuffer);

            byte[] dataArray1 = byteBuffer.array();
            /*System.out.println("After reading ----------------------------------");
            for(int i = 0; i < dataArray1.length; i++){
                System.out.print(dataArray1[i] + ",");
            }*/

//            System.out.println("Bytes Read: " + bytesRead + ", has remaining " + byteBuffer.hasRemaining());
            if(bytesRead == -1){
                socketChannel.close();
                selectionKey.cancel();
                return;
            }
//            System.out.println("[" + jobId + "] After Reading: " + byteBuffer.position() + ", " + byteBuffer.limit());

            if(!byteBuffer.hasRemaining()){ // we have read 8k of data
//                System.out.println("[" + jobId + "] Before Flipping: " + byteBuffer.position() + ", " + byteBuffer.limit());

                byteBuffer.flip();
//                System.out.println("[" + jobId + "] After Flipping: " + byteBuffer.position() + ", " + byteBuffer.limit());

               /* byte[] dataArray = byteBuffer.array();
                System.out.println("last bytes----------------------------------");
                for(int i = 0; i < dataArray.length; i++){
                    System.out.print(dataArray[i] + ",");
                }
                System.out.println("---------------------------------------------");*/
                ServerChannelChange serverChannelChange =
                        new ServerChannelChange(socketChannel, SelectionKey.OP_WRITE);
                server.addChannelChange(serverChannelChange);
            }
        } catch (IOException e) {
            try {
                e.printStackTrace();
                System.out.println("Cancelling Read key.");
                socketChannel.close();
                selectionKey.cancel();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
