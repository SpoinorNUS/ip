/**
 * Interprets raw user input as a command and its optional argument.
 */
public class Parser {

    /**
     * Identifies the commands understood by Turtley.
     */
    public enum CommandType {
        EMPTY,
        BYE,
        LIST,
        MARK,
        UNMARK,
        DELETE,
        TIMECHECK,
        TODO,
        DEADLINE,
        EVENT,
        UNKNOWN
    }

    /**
     * Represents one parsed user command.
     */
    public static final class Command {
        private final CommandType type;
        private final String argument;

        private Command(CommandType type, String argument) {
            this.type = type;
            this.argument = argument;
        }

        /**
         * Returns the parsed command type.
         *
         * @return the command type
         */
        public CommandType getType() {
            return type;
        }

        /**
         * Returns the trimmed text following the command keyword.
         *
         * @return the command argument, or an empty string when none was supplied
         */
        public String getArgument() {
            return argument;
        }
    }

    private Parser() {
        // Utility class; do not create instances.
    }

    /**
     * Parses one line of user input without executing it.
     *
     * @param input the raw user input
     * @return the identified command and its argument
     */
    public static Command parse(String input) {
        if (input == null || input.isEmpty()) {
            return command(CommandType.EMPTY, "");
        }
        if (input.equals("bye")) {
            return command(CommandType.BYE, "");
        }
        if (input.equals("list")) {
            return command(CommandType.LIST, "");
        }
        if (input.startsWith("mark ")) {
            return command(CommandType.MARK, input.substring(5).trim());
        }
        if (input.startsWith("unmark ")) {
            return command(CommandType.UNMARK, input.substring(7).trim());
        }
        if (input.equals("delete") || input.startsWith("delete ")) {
            return command(CommandType.DELETE, input.length() == 6 ? "" : input.substring(7).trim());
        }
        if (input.equals("timecheck") || input.startsWith("timecheck ")) {
            return command(CommandType.TIMECHECK, input.substring("timecheck".length()).trim());
        }
        if (input.equals("todo") || input.startsWith("todo ")) {
            return command(CommandType.TODO, input.length() == 4 ? "" : input.substring(5).trim());
        }
        if (input.startsWith("deadline ")) {
            return command(CommandType.DEADLINE, input.substring(9).trim());
        }
        if (input.startsWith("event ")) {
            return command(CommandType.EVENT, input.substring(6).trim());
        }
        return command(CommandType.UNKNOWN, "");
    }

    /**
     * Creates a parsed command with a non-null argument.
     *
     * @param type the command type
     * @param argument the command argument
     * @return the parsed command
     */
    private static Command command(CommandType type, String argument) {
        return new Command(type, argument);
    }
}
