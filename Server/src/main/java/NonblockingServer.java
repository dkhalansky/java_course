import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.List;
import java.util.Set;
import java.io.DataInputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;

import ru.ifmo.ct.khalansky.coursework.IntArrayOuterClass.IntArray;

public class NonblockingServer implements Server {

    private ServerSocketChannel server;
    private Selector selector;
    private Selector writeSelector;
    private ExecutorService threadPool;
    private List<ClientMeasurement> measurements;

    private Object writeLock = new Object();

    Thread writeThread;

    private class ClientAttachment {
        SocketChannel channel;
        ClientMeasurement measurement = new ClientMeasurement();
        ByteBuffer bufferForInt = ByteBuffer.allocate(4);
        ByteBuffer contentBuffer = null;
        int n = -1;
    }

    private void runClient(ClientAttachment attachment)
    throws IOException {
        ByteBuffer content = attachment.contentBuffer;
        byte[] message = content.array();
        IntArray intArray = IntArray.parseFrom(message);

        attachment.measurement.beginProcessing();
        List<Integer> numbers = intArray.getArrayList();
        List<Integer> result = Sorter.sort(numbers);
        attachment.measurement.endProcessing();

        IntArray.Builder array = IntArray.newBuilder();
        array.setN(result.size());
        array.addAllArray(result);
        byte[] query = array.build().toByteArray();

        attachment.bufferForInt.rewind();
        attachment.bufferForInt.putInt(query.length);
        attachment.bufferForInt.flip();
        attachment.contentBuffer = ByteBuffer.wrap(query);


        synchronized (writeLock) {
            attachment.channel.register(
                writeSelector, SelectionKey.OP_WRITE, attachment);
            writeLock.notify();
        }
    }

    private Runnable readRunnable = () -> {
        while (true) {
            if (Thread.interrupted()) {
                return;
            }

            Set<SelectionKey> keys;
            try {
                selector.select();
                keys = selector.selectedKeys();
            } catch (ClosedSelectorException e) {
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Iterator<SelectionKey> it = keys.iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                if (key.isAcceptable()) {
                    ServerSocketChannel server =
                        (ServerSocketChannel)(key.channel());
                    try {
                        SocketChannel client = server.accept();
                        if (client != null) {
                            client.configureBlocking(false);
                            ClientAttachment attachment =
                                new ClientAttachment();
                            attachment.channel = client;
                            attachment.measurement.beginSession();
                            measurements.add(attachment.measurement);
                            client.register(
                                selector,
                                SelectionKey.OP_READ,
                                attachment);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else if (key.isReadable()) {
                    SocketChannel client = (SocketChannel)(key.channel());
                    ClientAttachment attachment =
                        (ClientAttachment)(key.attachment());
                    if (attachment.n == -1) {
                        try {
                            int res = client.read(attachment.bufferForInt);
                            if (res < 0) {
                                key.cancel();
                                attachment.measurement.endSession();
                            } else {
                                attachment.measurement.beginInteraction();
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        if (attachment.bufferForInt.position() == 4) {
                            attachment.bufferForInt.flip();
                            attachment.n = attachment.bufferForInt.getInt();
                            attachment.contentBuffer = ByteBuffer.allocate(
                                attachment.n);
                        }
                    }
                    if (attachment.n > 0) {
                        try {
                            client.read(attachment.contentBuffer);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        if (attachment.contentBuffer.position() == attachment.n)
                        {
                            attachment.contentBuffer.flip();
                            attachment.n = -2;
                            Runnable task = () -> {
                                try {
                                    runClient(attachment);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            };
                            threadPool.submit(task);
                        }
                    }
                }
                it.remove();
            }
        }
    };

    private Runnable writeRunnable = () -> {
        while (true) {
            if (Thread.interrupted()) {
                return;
            }

            Set<SelectionKey> keys;
            try {
                int ready = writeSelector.selectNow();
                if (ready == 0) {
                    synchronized (writeLock) {
                        while (writeSelector.keys().isEmpty()) {
                            writeLock.wait();
                        }
                    }
                    writeSelector.select();
                }
                keys = writeSelector.selectedKeys();
            } catch (ClosedSelectorException e) {
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                return;
            }

            Iterator<SelectionKey> it = keys.iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                if (key.isWritable()) {
                    SocketChannel client = (SocketChannel)(key.channel());
                    ClientAttachment attachment =
                        (ClientAttachment)(key.attachment());
                    ByteBuffer[] buffers = {
                        attachment.bufferForInt,
                        attachment.contentBuffer
                    };
                    try {
                        client.write(buffers);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (attachment.bufferForInt.position() == 4 &&
                        attachment.contentBuffer.position() ==
                        attachment.contentBuffer.capacity())
                    {
                        attachment.measurement.endInteraction();
                        attachment.bufferForInt.rewind();
                        attachment.n = -1;
                        key.cancel();
                    }
                }
                it.remove();
            }
        }
    };

    public void run(SocketAddress address, List<ClientMeasurement> measurements,
        CountDownLatch latch) throws IOException
    {
        this.measurements = measurements;
        threadPool = Executors.newFixedThreadPool(8);

        server = ServerSocketChannel.open();
        server.socket().bind(address);
        server.configureBlocking(false);
        selector = Selector.open();
        writeSelector = Selector.open();

        SelectionKey serverKey = server.register(
            selector, SelectionKey.OP_ACCEPT);

        writeThread = new Thread(writeRunnable);
        writeThread.start();

        latch.countDown();
        readRunnable.run();
    }

    public void shutdown() {
        try {
            selector.close();
            threadPool.shutdown();
            writeThread.interrupt();
            writeSelector.close();
            try {
                while (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                }
                writeThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            server.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
