package cs455.scale.client;

import cs455.scale.util.LoggingUtil;

/**
 * Author: Thilina
 * Date: 3/2/14
 */
public class Client {

    private String serverHost;
    private int serverPort;
    private int messageRate;

    public Client(String serverHost, int serverPort, int messageRate) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.messageRate = messageRate;
    }

    public static void main(String[] args) {
        if(args.length < 3){
            LoggingUtil.logError("Required arguments are missing. Expected format " +
                    "\'java cs455.scaling.client.Client server-host server-port message-rate\' \n");
        }

        String hostName = args[0];
        int port = Integer.parseInt(args[1]);
        int msgRate = Integer.parseInt(args[2]);

        Client client = new Client(hostName, port, msgRate);
    }
}
