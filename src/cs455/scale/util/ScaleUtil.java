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

    public static InetAddress getHostInetAddress() throws UnknownHostException {
        InetAddress inetAddr = InetAddress.getLocalHost();
        return inetAddr;
    }

    public static byte[] getPayLoad(){
        byte[] payload = new byte[8*1024];
        random.nextBytes(payload);
        return payload;
    }

}
