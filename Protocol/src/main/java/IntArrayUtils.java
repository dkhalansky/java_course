package ru.ifmo.ct.khalansky.coursework;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.List;
import ru.ifmo.ct.khalansky.coursework.IntArrayOuterClass.IntArray;

public class IntArrayUtils {

    public static IntArray fromStream(DataInputStream is) throws IOException {
        int n = is.readInt();
        int nRead = 0;
        byte[] ans = new byte[n];
        while (nRead < n) {
            nRead += is.read(ans, nRead, n - nRead);
        }
        return IntArray.parseFrom(ans);
    }

    public static void toStream(DataOutputStream os, List<Integer> list)
    throws IOException {
        IntArray.Builder array = IntArray.newBuilder();
        array.setN(list.size());
        array.addAllArray(list);
        byte[] query = array.build().toByteArray();
        os.writeInt(query.length);
        os.write(query);
    }

}
