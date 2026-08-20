/**
 * A task that must be completed by a specified time.
 */
public class Deadline extends Task {

    private final String by;

    /**
     * Creates an unfinished deadline.
     *
     * @param description the task description
     * @param by the deadline text
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the deadline type icon.
     *
     * @return {@code D}
     */
    @Override
    public String getTypeIcon() {
        return "D";
    }

    /**
     * Returns the deadline text.
     *
     * @return the deadline text
     */
    public String getBy() {
        return by;
    }

    /**
     * Returns the display form including the deadline.
     *
     * @return the formatted deadline
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + by + ")";
    }
}
