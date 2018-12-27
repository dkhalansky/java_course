import java.io.Closeable;
import java.io.IOException;
import java.io.EOFException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.List;
import ru.ifmo.ct.khalansky.coursework.IntArrayUtils;

class ListExchanger implements Closeable {

    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;

    public ListExchanger(Socket socket) throws IOException {
        this.socket = socket;
        this.is = new DataInputStream(socket.getInputStream());
        this.os = new DataOutputStream(socket.getOutputStream());
    }

    public List<Integer> getData() throws IOException {
        try {
            return IntArrayUtils.fromStream(is).getArrayList();
        } catch (EOFException e) {
            return null;
        }
    }

    public void sendData(List<Integer> numbers)
    throws IOException {
        IntArrayUtils.toStream(os, numbers);
    }

    public void close() throws IOException {
        is.close();
        os.close();
    }

}
