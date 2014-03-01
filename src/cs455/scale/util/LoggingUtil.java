package cs455.scale.util;

/**
 * Author: Thilina
 * Date: 2/27/14
 * Provides logging facility.
 * Used by both the server and client packages.
 */
public class LoggingUtil {

    public static void logInfo(String infoMessage){
        System.err.println("[Info]" + infoMessage);
    }

    public static void logError(String errorMessage){
        System.err.println("[Error]" + errorMessage);
    }

    public static void logError(String errorMessage, Throwable e){
        System.err.println("[Error]" + errorMessage);
        e.printStackTrace();
    }

}
