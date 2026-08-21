/**
 * A task that takes place during a specified time range.
 */
public class Event extends Task {

    private final String from;
    private final String to;

    /**
     * Creates an unfinished event.
     *
     * @param description the event description
     * @param from the event start time
     * @param to the event end time
     */
    public Event(String description, String from, String to) {
        super(TaskType.EVENT, description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event start time.
     *
     * @return the start time
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event end time.
     *
     * @return the end time
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns the display form including the event time range.
     *
     * @return the formatted event
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
