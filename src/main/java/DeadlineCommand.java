/**
 * Adds a deadline task.
 */
public class DeadlineCommand extends AddCommand {

    private final String input;

    /**
     * Creates a deadline command.
     *
     * @param input the description and deadline text
     */
    public DeadlineCommand(String input) {
        this.input = input;
    }

    @Override
    protected Task createTask() {
        int byIndex = input.indexOf(" /by ");
        if (byIndex <= 0 || byIndex + 5 >= input.length()) {
            throw invalidFormat();
        }

        String description = input.substring(0, byIndex).trim();
        String by = input.substring(byIndex + 5).trim();
        if (description.isEmpty() || by.isEmpty()) {
            throw invalidFormat();
        }
        return new Deadline(description, DateTimeParser.parse(by));
    }

    private TurtleyException invalidFormat() {
        return new TurtleyException("Invalid format. Use: deadline <description> /by <date>");
    }
}
