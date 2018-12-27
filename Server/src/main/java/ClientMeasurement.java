import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class ClientMeasurement {

    private List<Measurement> interaction = new ArrayList<>();
    private List<Measurement> processing = new ArrayList<>();

    private Measurement currentInteraction;
    private Measurement currentProcessing;

    public final Measurement session = new Measurement();
    public final List<Measurement> interactions =
        Collections.unmodifiableList(interaction);
    public final List<Measurement> processings =
        Collections.unmodifiableList(processing);

    public ClientMeasurement() {
    }

    public void beginSession() {
        session.begin();
    }

    public void endSession() {
        session.end();
    }

    public void beginInteraction() {
        currentInteraction = new Measurement();
        currentInteraction.begin();
    }

    public void endInteraction() {
        currentInteraction.end();
        interaction.add(currentInteraction);
        currentInteraction = null;
    }

    public void beginProcessing() {
        currentProcessing = new Measurement();
        currentProcessing.begin();
    }

    public void endProcessing() {
        currentProcessing.end();
        processing.add(currentProcessing);
        currentProcessing = null;
    }

    public Measurement getSession() {
        return session;
    }

    public List<Measurement> getProcessingMeasurements() {
        return Collections.unmodifiableList(processing);
    }

    public List<Measurement> getInteractionMeasurements() {
        return Collections.unmodifiableList(interaction);
    }

}
