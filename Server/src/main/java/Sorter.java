import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

public class Sorter {

    public static List<Integer> sort(Collection<? extends Integer> numbers) {
        List<Integer> r = new ArrayList<>(numbers);
        for (int i = 1; i < r.size(); ++i) {
            int j = i - 1;
            while (j >= 0 && r.get(j) > r.get(j+1)) {
                int c = r.get(j);
                r.set(j, r.get(j+1));
                r.set(j+1, c);
                --j;
            }
        }
        return r;
    }

}
