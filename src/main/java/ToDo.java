/**
 * A task without a deadline or event time range.
 */
public class ToDo extends Task {

    /**
     * Creates an unfinished to-do task.
     *
     * @param description the task description
     */
    public ToDo(String description) {
        super(description);
    }
}
