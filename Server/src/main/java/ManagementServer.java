import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

class ManagementServer {

    private List<ClientMeasurement> measurements = new ArrayList<>();
    private Thread serverThread;
    private Server serverProvider;

    private void shutdown() {
        if (serverThread == null)
            return;

        serverThread.interrupt();
        serverProvider.shutdown();
        while (true) {
            try {
                serverThread.join();
                break;
            } catch (InterruptedException e) {
            }
        }
        serverThread = null;
        serverProvider = null;
    }

    public void runManagement(
        InetSocketAddress address,
        InetSocketAddress serverAddress)
    throws IOException {

        ServerSocket server = new ServerSocket();
        server.bind(address);
        while (true) {
            try (Socket client = server.accept();
                DataOutputStream os =
                    new DataOutputStream(client.getOutputStream());
                DataInputStream is =
                    new DataInputStream(client.getInputStream()))
            {
                byte command = is.readByte();
                switch (command) {
                    case 1:
                        shutdown();
                        byte serverType = is.readByte();
                        switch (serverType) {
                            case 1:
                                serverProvider =
                                    new ExclusiveThreadedServer();
                                break;
                            case 2:
                                serverProvider =
                                    new SharinglyThreadedServer();
                                break;
                            case 3:
                                serverProvider =
                                    new NonblockingServer();
                                break;
                            case 4:
                                // TODO
                                // serverProvider =
                                //     new AsyncServer();
                                break;
                            default:
                                break;
                        }
                        CountDownLatch latch = new CountDownLatch(1);
                        measurements = new ArrayList<>();
                        Runnable serverRunnable = () -> {
                            try {
                                serverProvider.run(
                                    serverAddress, measurements, latch);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        };
                        serverThread = new Thread(serverRunnable);
                        serverThread.start();
                        try {
                            latch.await();
                        } catch (InterruptedException e) {
                        }
                        os.writeByte(1);
                        break;
                    case 2:
                        shutdown();
                        break;
                    case 3:
                        MeasurementSummary summary =
                            MeasurementSummary.summarize(measurements);
                        os.writeLong(summary.getMeanClient());
                        os.writeLong(summary.getMeanInteraction());
                        os.writeLong(summary.getMeanProcessing());
                        break;
                    case 4:
                        return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
