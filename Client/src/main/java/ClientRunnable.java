package ru.ifmo.ct.khalansky.coursework.client;
import java.util.function.Supplier;
import java.io.IOException;
import java.util.List;
import java.net.Socket;

class ClientRunnable implements Runnable {

    final TaskPipeline<List<Integer>> pipeline;
    final ListSender actor;
    final int numberOfIterations;

    public ClientRunnable(Socket socket, int listLength, long cooldown,
    int numberOfIterations)
    throws IOException {
        Supplier<List<Integer>> task = new RandomArraySupplier(listLength);
        actor = new ListSender(socket);
        pipeline = new TaskPipeline<>(task, actor, cooldown);
        this.numberOfIterations = numberOfIterations;
    }

    public void run() {
        pipeline.run(numberOfIterations);
        try {
            actor.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
