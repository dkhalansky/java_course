import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

public class MeasurementSummary {

    private long meanClient;
    private long meanInteraction;
    private long meanProcessing;

    public MeasurementSummary(
        long meanClient, long meanInteraction, long meanProcessing)
    {
        this.meanClient = meanClient;
        this.meanInteraction = meanInteraction;
        this.meanProcessing = meanProcessing;
    }

    public static MeasurementSummary summarize(
        List<ClientMeasurement> measurements)
    {
        long lastClientStart = measurements.stream().
            mapToLong((cms) -> cms.getSession().getStart()).
            max().
            orElse(0);
        long firstClientEnd = measurements.stream().
            mapToLong((cms) -> cms.getSession().getEnd()).
            min().
            orElse(-1);

        OptionalDouble meanClient = measurements.stream().
            mapToLong((cms) -> cms.getSession().getDuration()).
            average();
        OptionalDouble meanProcessing = measurements.stream().
            flatMap((cms) -> cms.getProcessingMeasurements().stream()).
            filter((ms) -> ms.isInside(lastClientStart, firstClientEnd)).
            mapToLong(Measurement::getDuration).
            average();
        OptionalDouble meanInteraction = measurements.stream().
            flatMap((cms) -> cms.getInteractionMeasurements().stream()).
            filter((ms) -> ms.isInside(lastClientStart, firstClientEnd)).
            mapToLong(Measurement::getDuration).
            average();

        return new MeasurementSummary(
            optionalDoubleToMs(meanClient),
            optionalDoubleToMs(meanInteraction),
            optionalDoubleToMs(meanProcessing));
    }

    private static long optionalDoubleToMs(OptionalDouble od) {
        return Math.round(od.orElse(-1000000) / 1000000);
    }

    public long getMeanClient() {
        return meanClient;
    }

    public long getMeanInteraction() {
        return meanInteraction;
    }

    public long getMeanProcessing() {
        return meanProcessing;
    }

}
