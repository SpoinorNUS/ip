/**
 * Converts raw user input into executable commands.
 */
public class Parser {

    private Parser() {
        // Utility class; do not create instances.
    }

    /**
     * Parses one line of user input.
     *
     * @param input the raw user input
     * @return the executable command represented by the input
     */
    public static Command parse(String input) {
        if (input == null || input.isEmpty()) {
            return new EmptyCommand();
        }
        if (input.equals("bye")) {
            return new ExitCommand();
        }
        if (input.equals("list")) {
            return new ListCommand();
        }
        if (input.startsWith("mark ")) {
            return new MarkCommand(input.substring(5).trim());
        }
        if (input.startsWith("unmark ")) {
            return new UnmarkCommand(input.substring(7).trim());
        }
        if (input.equals("delete") || input.startsWith("delete ")) {
            return new DeleteCommand(input.length() == 6 ? "" : input.substring(7).trim());
        }
        if (input.equals("timecheck") || input.startsWith("timecheck ")) {
            return new TimecheckCommand(input.substring("timecheck".length()).trim());
        }
        if (input.equals("todo") || input.startsWith("todo ")) {
            return new TodoCommand(input.length() == 4 ? "" : input.substring(5).trim());
        }
        if (input.startsWith("deadline ")) {
            return new DeadlineCommand(input.substring(9).trim());
        }
        if (input.startsWith("event ")) {
            return new EventCommand(input.substring(6).trim());
        }
        return new UnknownCommand();
    }
}
