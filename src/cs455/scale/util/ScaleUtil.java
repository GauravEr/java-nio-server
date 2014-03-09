package cs455.scale.util;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
            inetAddr = InetAddress.getLoopbackAddress();
        }
        return inetAddr;
    }

    public static byte[] getPayLoad(){
        byte[] payload = new byte[1024*8];
        random.nextBytes(payload);
        return payload;
    }

    public static byte[] SHA1FromBytes(byte[] bytes) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            LoggingUtil.logError(ScaleUtil.class, "Error generating the hash", e);
        }
        return digest.digest(bytes);
    }

    public static String hexStringFromBytes(byte[] bytes){
        BigInteger bigInteger = new BigInteger(1, bytes);
        return bigInteger.toString();
    }

}
