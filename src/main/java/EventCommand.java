/**
 * Adds an event task.
 */
public class EventCommand extends AddCommand {

    private final String input;

    /**
     * Creates an event command.
     *
     * @param input the description and event time range
     */
    public EventCommand(String input) {
        this.input = input;
    }

    @Override
    protected Task createTask() {
        int fromIndex = input.indexOf(" /from ");
        int toIndex = fromIndex < 0 ? -1 : input.indexOf(" /to ", fromIndex + 6);
        if (fromIndex <= 0 || toIndex <= fromIndex + 6 || toIndex + 5 >= input.length()) {
            throw invalidFormat();
        }

        String description = input.substring(0, fromIndex).trim();
        String from = input.substring(fromIndex + 6, toIndex).trim();
        String to = input.substring(toIndex + 5).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw invalidFormat();
        }
        return new Event(description, DateTimeParser.parse(from), DateTimeParser.parse(to));
    }

    private TurtleyException invalidFormat() {
        return new TurtleyException("Invalid format. Use: event <description> /from <start> /to <end>");
    }
}
