package ru.ifmo.ct.khalansky.coursework.client;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.Closeable;
import java.net.Socket;
import java.util.List;
import java.util.function.Consumer;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import ru.ifmo.ct.khalansky.coursework.IntArrayOuterClass.IntArray;
import ru.ifmo.ct.khalansky.coursework.IntArrayUtils;

class ListSender implements Closeable, Consumer<List<Integer>> {

    Socket socket;
    DataOutputStream output;
    DataInputStream input;

    public ListSender(Socket socket) throws IOException {
        this.socket = socket;
        output = new DataOutputStream(socket.getOutputStream());
        input = new DataInputStream(socket.getInputStream());
    }

    public void accept(List<Integer> list) {
        try {
            IntArrayUtils.toStream(output, list);
            IntArray response = IntArrayUtils.fromStream(input);
            List<Integer> answer = response.getArrayList();
            for (int i = 0; i < list.size(); ++i) {
                list.set(i, answer.get(i));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() throws IOException {
        output.close();
        input.close();
        socket.close();
    }

}
