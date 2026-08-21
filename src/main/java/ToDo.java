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
        super(TaskType.TODO, requireDescription(description));
    }

    /**
     * Ensures that a to-do has a meaningful name before it is created.
     *
     * @param description the proposed task description
     * @return the unchanged non-blank description
     * @throws TurtleyException if the description is missing or blank
     */
    private static String requireDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new TurtleyException("Invalid format. Use: todo <description>");
        }
        return description;
    }
}
