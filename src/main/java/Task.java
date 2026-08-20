/**
 * Represents a general task in Turtley's task list.
 */
public class Task {

    protected String description;
    protected boolean isDone;
    private final String taskType;

    /**
     * Creates an unfinished task with the given description.
     *
     * @param description the task description
     */
    public Task(String description) {
        this("T", description);
    }

    /**
     * Creates a task with a caller-supplied type icon.
     *
     * <p>This constructor is retained for compatibility with the earlier
     * two-argument task creation code. New specialized tasks should use
     * {@link Deadline} or {@link Event} instead.</p>
     *
     * @param taskType the type icon to display
     * @param description the task description
     */
    public Task(String taskType, String description) {
        this.taskType = taskType;
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns this task's type icon.
     *
     * @return the task type icon
     */
    public String getTypeIcon() {
        return taskType;
    }

    /**
     * Returns the symbol used to display this task's completion status.
     *
     * @return {@code X} for a completed task, or a space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns this task's description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the display form of this task.
     *
     * @return the type icon, status icon, and description
     */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
