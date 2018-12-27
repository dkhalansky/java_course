import java.util.List;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketAddress;
import ru.ifmo.ct.khalansky.coursework.IntArrayUtils;

public interface BlockingServer extends Server {

    void runClient(ListExchanger client, ClientMeasurement measurement)
    throws IOException;

    default public void runBlocking(
        ServerSocket server, List<ClientMeasurement> measurements,
        List<Thread> clientThreads)
    throws IOException {
        while (true) {
            if (Thread.interrupted()) {
                return;
            }

            Socket client;
            try {
                client = server.accept();
            } catch (SocketException e) {
                return;
            }

            ClientMeasurement measurement = new ClientMeasurement();
            measurements.add(measurement);
            measurement.beginSession();

            ListExchanger exchanger = new ListExchanger(client);

            Thread clientThread = new Thread(() ->
            {
                try {
                    runClient(exchanger, measurement);
                } catch (SocketException e) {
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            clientThreads.add(clientThread);
            clientThread.start();
        }
    }

}

