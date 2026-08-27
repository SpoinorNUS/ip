/**
 * Adds a to-do task.
 */
public class TodoCommand extends AddCommand {

    private final String description;

    /**
     * Creates a to-do command.
     *
     * @param description the to-do description
     */
    public TodoCommand(String description) {
        this.description = description;
    }

    @Override
    protected Task createTask() {
        return new ToDo(description);
    }
}
