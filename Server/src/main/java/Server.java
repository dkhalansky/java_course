import java.io.IOException;
import java.util.List;
import java.net.SocketAddress;
import java.util.concurrent.CountDownLatch;

public interface Server {

    void run(SocketAddress address, List<ClientMeasurement> measurements,
        CountDownLatch latch) throws IOException;

    void shutdown();

}
