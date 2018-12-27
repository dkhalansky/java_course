package ru.ifmo.ct.khalansky.coursework.client;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.io.IOException;

public class ClientFacade {

    public static void runClients(
        InetAddress address, short port, int n, int m, long delta, int x)
    throws IOException
    {
        /* Should probably be okay since all these threads will be sleeping
        most of the time. */
        ExecutorService service = Executors.newFixedThreadPool(m);
        for (int i = 0; i < m; ++i) {
            Socket socket = new Socket(address, port);
            ClientRunnable runnable = new ClientRunnable(socket, n, delta, x);
            service.submit(runnable);
        }
        service.shutdown();
        try {
            while (!service.awaitTermination(50, TimeUnit.SECONDS)) {
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
