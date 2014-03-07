package cs455.scale.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * Author: Thilina
 * Date: 3/2/14
 */
public class ScaleUtil {

    private static Random random = new Random(System.currentTimeMillis());

    public static InetAddress getHostInetAddress() {
        InetAddress inetAddr = null;
        try {
            inetAddr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            try {
                inetAddr = InetAddress.getByName("localhost");
            } catch (UnknownHostException ignore) {

            }
        }
        return inetAddr;
    }

    public static byte[] getPayLoad(){
        byte[] payload = new byte[1024*8];
        random.nextBytes(payload);
        return payload;
    }

}
