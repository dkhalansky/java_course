import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

class ExclusiveThreadedServer implements BlockingServer {

    private ServerSocket server;
    private boolean done = false;

    public void run(SocketAddress address, List<ClientMeasurement> measurements,
        CountDownLatch latch) throws IOException
    {
        server = new ServerSocket();
        server.bind(address);
        latch.countDown();
        List<Thread> clientThreads = new ArrayList<>();
        runBlocking(server, measurements, clientThreads);
        for (Thread thread : clientThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
        }
        synchronized (this) {
            done = true;
            this.notify();
        }
    }

    public void shutdown() {
        try {
            server.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            synchronized (this) {
                while (!done) {
                    this.wait();
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void runClient(ListExchanger client, ClientMeasurement measurement)
    throws IOException
    {
        try {
            while (true) {
                measurement.beginInteraction();
                List<Integer> numbers = client.getData();
                if (numbers == null) {
                    break;
                }
                measurement.beginProcessing();
                List<Integer> result = Sorter.sort(numbers);
                measurement.endProcessing();
                client.sendData(result);
                measurement.endInteraction();
            }
            measurement.endSession();
        } finally {
            client.close();
        }
    }

}
