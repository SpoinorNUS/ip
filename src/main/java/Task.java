/**
 * Represents a task in Turtley's task list.
 * (All written by ChatGPT)
 */
public class Task {

    protected String taskType;
    protected String description;
    protected boolean isDone;

    /**
     * Creates an unfinished task with the given description.
     *
     * @param description the task description
     */
    public Task(String taskType, String description) {
        this.taskType = taskType;
        this.description = description;
        this.isDone = false;
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
     * @return the status icon followed by the task description
     */
    @Override
    public String toString() {
        return "[" + taskType + "]" + "[" + getStatusIcon() + "] " + description;
    }
}
