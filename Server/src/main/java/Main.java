import java.net.*;
import java.io.IOException;

class Main {

    public static void main(String[] args) throws IOException {
        String managementHost = args[0];
        short managementPort = Short.parseShort(args[1]);
        InetSocketAddress address = new InetSocketAddress(
            managementHost, managementPort);

        String serverHost = args[0];
        short serverPort = Short.parseShort(args[3]);
        InetSocketAddress serverAddress = new InetSocketAddress(
            serverHost, serverPort);

        ManagementServer management = new ManagementServer();
        management.runManagement(address, serverAddress);
    }

}
