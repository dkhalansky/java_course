import java.net.SocketAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class SharinglyThreadedServer implements BlockingServer {

    private ServerSocket server;
    private ExecutorService threadPool;
    private List<Thread> clientThreads = new ArrayList<>();

    public void run(
        SocketAddress address,
        List<ClientMeasurement> measurements,
        CountDownLatch latch) throws IOException
    {
        threadPool = Executors.newFixedThreadPool(8);
        server = new ServerSocket();
        server.bind(address);
        latch.countDown();
        runBlocking(server, measurements, clientThreads);
    }

    public void runClient(ListExchanger client, ClientMeasurement measurement)
    throws IOException
    {
        try {
            ExecutorService sender = Executors.newSingleThreadExecutor();
            while (true) {
                measurement.beginInteraction();
                List<Integer> numbers = client.getData();
                if (numbers == null) {
                    break;
                }
                threadPool.submit(() -> {
                    measurement.beginProcessing();
                    List<Integer> result = Sorter.sort(numbers);
                    measurement.endProcessing();
                    sender.submit(() -> {
                        try {
                            client.sendData(result);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        measurement.endInteraction();
                    });
                });
            }
            sender.shutdown();
            try {
                while (!sender.awaitTermination(60, TimeUnit.SECONDS)) {
                }
            } catch (InterruptedException e) {
            }
            measurement.endSession();
        } finally {
            client.close();
        }
    }

    public void shutdown() {
        try {
            server.close();
            threadPool.shutdown();
            while (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
            }
            for (Thread thread : clientThreads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
