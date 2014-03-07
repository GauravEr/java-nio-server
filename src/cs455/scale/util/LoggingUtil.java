package cs455.scale.util;

/**
 * Author: Thilina
 * Date: 2/27/14
 * Provides logging facility.
 * Used by both the server and client packages.
 */
public class LoggingUtil {

    public static void logInfo(Class className, String infoMessage){
        System.err.println("[Info]" + className + ":" + infoMessage);
    }

    public static void logError(Class className, String errorMessage){
        System.err.println("[Error]" + className + errorMessage);
    }

    public static void logError(Class className, String errorMessage, Throwable e){
        System.err.println("[Error]" + className + errorMessage);
        e.printStackTrace();
    }

}
