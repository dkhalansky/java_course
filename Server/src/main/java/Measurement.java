import java.time.Instant;

public class Measurement {

    private long start;
    private long end;

    public Measurement() {
    }

    public Measurement(long start, long end) {
        this.start = start;
        this.end = end;
    }

    public void begin() {
        start = System.nanoTime();
    }

    public void end() {
        end = System.nanoTime();
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public long getDuration() {
        return end - start;
    }

    public boolean isInside(long start, long end) {
        return start <= this.start && end >= this.end;
    }

}
