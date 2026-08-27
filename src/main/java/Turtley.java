import java.util.Scanner;
import java.time.temporal.Temporal;

public class Turtley {

    private static final Ui ui = new Ui();
    private static final TaskList taskList = new TaskList();

    /**
     * Saves the current list and reports persistence errors without terminating the chatbot.
     *
     * @return {@code true} if the list was saved successfully
     */
    private static boolean saveTaskList() {
        try {
            Storage.save(taskList.asList());
            return true;
        } catch (TurtleyException exception) {
            ui.showError(exception);
            return false;
        }
    }

    //Adds a task object to the task list.
    public static void add(Task newTask) {
        if (newTask == null) {
            ui.showError(new TurtleyException("Cannot add a null task."));
            return;
        }
        if (taskList.isFull()) {
            ui.showTaskListFull();
            return;
        }
        taskList.add(newTask);
        if (!saveTaskList()) {
            taskList.remove(taskList.size() - 1);
            return;
        }
        ui.showTaskAdded(newTask, taskList.size());
    }

    //Adds a task using its enum-based type.
    public static void add(TaskType taskType, String input) {
        if (taskType == null) {
            ui.showError(new TurtleyException("Cannot add a task with an invalid type."));
            return;
        }
        if (taskType == TaskType.TODO) {
            try {
                add(new ToDo(input));
            } catch (TurtleyException exception) {
                ui.showError(exception);
            }
        } else {
            add(new Task(taskType, input));
        }
    }

    //Parses and adds a deadline command's description and /by field. (Written by ChatGPT)
    private static void addDeadline(String input) {
        int byIndex = input.indexOf(" /by ");
        if (byIndex <= 0 || byIndex + 5 >= input.length()) {
            showInvalidTaskFormat("deadline <description> /by <date>");
            return;
        }

        String description = input.substring(0, byIndex).trim();
        String by = input.substring(byIndex + 5).trim();
        if (description.isEmpty() || by.isEmpty()) {
            showInvalidTaskFormat("deadline <description> /by <date>");
            return;
        }
        try {
            add(new Deadline(description, DateTimeParser.parse(by)));
        } catch (TurtleyException exception) {
            ui.showError(exception);
        }
    }

    //Parses and adds an event command's description, /from field, and /to field. (Written by ChatGPT)
    private static void addEvent(String input) {
        int fromIndex = input.indexOf(" /from ");
        int toIndex = fromIndex < 0 ? -1 : input.indexOf(" /to ", fromIndex + 6);
        if (fromIndex <= 0 || toIndex <= fromIndex + 6 || toIndex + 5 >= input.length()) {
            showInvalidTaskFormat("event <description> /from <start> /to <end>");
            return;
        }

        String description = input.substring(0, fromIndex).trim();
        String from = input.substring(fromIndex + 6, toIndex).trim();
        String to = input.substring(toIndex + 5).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            showInvalidTaskFormat("event <description> /from <start> /to <end>");
            return;
        }
        try {
            add(new Event(description, DateTimeParser.parse(from), DateTimeParser.parse(to)));
        } catch (TurtleyException exception) {
            ui.showError(exception);
        }
    }

    //Prints a helpful message when a structured task command is malformed.(Written by ChatGPT)
    private static void showInvalidTaskFormat(String format) {
        ui.showError(new TurtleyException("Invalid format. Use: " + format));
    }

    //Reads from the taskList
    public static void list() {
        ui.showTaskList(taskList.asList());
    }

    //(Written by ChatGPT)
    /**
     * Lists deadlines and events whose relevant date/time is on or before the cutoff.
     * For events, the start date/time determines when the event takes place.
     *
     * @param input the date/time entered after {@code timecheck}
     */
    public static void timecheck(String input) {
        try {
            Temporal cutoff = DateTimeParser.parse(input);
            String cutoffText = DateTimeParser.format(cutoff);
            boolean hasMatchingTask = false;

            ui.showTimecheckHeader(cutoffText);
            for (int i = 0; i < taskList.size(); i++) {
                Task task = taskList.get(i);
                boolean isDeadline = task instanceof Deadline deadline
                        && DateTimeParser.isOnOrBefore(deadline.getBy(), cutoff);
                boolean isEvent = task instanceof Event event
                        && DateTimeParser.isOnOrBefore(event.getFrom(), cutoff);
                if (isDeadline || isEvent) {
                    hasMatchingTask = true;
                    ui.showTask(i, task);
                }
            }
            if (!hasMatchingTask) {
                ui.showNoMatchingTasks();
            }
            ui.showSeparator();
        } catch (TurtleyException exception) {
            ui.showError(exception);
        }
    }

    //Marks the task at the given one-based list index as done. (Written by ChatGPT)
    public static void mark(String input) {
        try {
            int taskIndex = parseTaskIndex(input);
            if (taskIndex < 0 || taskIndex >= taskList.size()) {
                throw new TurtleyException("Task number is not in your list.");
            }

            Task task = taskList.get(taskIndex);
            boolean wasDone = task.isDone();
            task.markAsDone();
            if (!saveTaskList()) {
                if (!wasDone) {
                    task.markAsNotDone();
                }
                return;
            }
            ui.showTaskMarked(taskList.get(taskIndex));
        } catch (TurtleyException exception) {
            ui.showError(exception);
        }
    }

    //Marks the task at the given one-based list index as not done. (Written by ChatGPT)
    public static void unmark(String input) {
        try {
            int taskIndex = parseTaskIndex(input);
            if (taskIndex < 0 || taskIndex >= taskList.size()) {
                throw new TurtleyException("Task number is not in your list.");
            }

            Task task = taskList.get(taskIndex);
            boolean wasDone = task.isDone();
            task.markAsNotDone();
            if (!saveTaskList()) {
                if (wasDone) {
                    task.markAsDone();
                }
                return;
            }
            ui.showTaskUnmarked(taskList.get(taskIndex));
        } catch (TurtleyException exception) {
            ui.showError(exception);
        }
    }

    /**
     * Deletes the task at the given one-based list index.
     *
     * @param input the task number entered by the user
     */ //(Written by ChatGPT)
    public static void delete(String input) {
        try {
            int taskIndex = parseTaskIndex(input);
            if (taskIndex < 0 || taskIndex >= taskList.size()) {
                throw new TurtleyException("Task number is not in your list.");
            }

            Task deletedTask = taskList.remove(taskIndex);
            if (!saveTaskList()) {
                taskList.add(taskIndex, deletedTask);
                return;
            }
            ui.showTaskDeleted(deletedTask, taskList.size());
        } catch (TurtleyException exception) {
            ui.showError(exception);
        }
    }

    /**
     * Parses a one-based task number into the zero-based index used internally.
     *
     * @param input the task number entered by the user
     * @return the corresponding zero-based task index
     * @throws TurtleyException if the input is not a valid integer
     */
    private static int parseTaskIndex(String input) {
        if (input == null || input.isEmpty()) {
            throw new TurtleyException("Please provide a valid task number.");
        }

        int sign = 1;
        int digitStart = 0;
        char firstCharacter = input.charAt(0);
        if (firstCharacter == '-' || firstCharacter == '+') {
            sign = firstCharacter == '-' ? -1 : 1;
            digitStart = 1;
        }
        if (digitStart == input.length()) {
            throw new TurtleyException("Please provide a valid task number.");
        }

        long maximumAbsoluteValue = sign < 0 ? 2_147_483_648L : Integer.MAX_VALUE;
        long absoluteValue = 0;
        for (int i = digitStart; i < input.length(); i++) {
            char currentCharacter = input.charAt(i);
            if (currentCharacter < '0' || currentCharacter > '9') {
                throw new TurtleyException("Please provide a valid task number.");
            }
            int digit = currentCharacter - '0';
            if (absoluteValue > (maximumAbsoluteValue - digit) / 10) {
                throw new TurtleyException("Please provide a valid task number.");
            }
            absoluteValue = absoluteValue * 10 + digit;
        }

        return (int) (sign * absoluteValue) - 1;
    }

    //Terminates the app
    public static void bye() {
        ui.showGoodbye();
    }

    //Waits for input from the user
    public static boolean prompt(Scanner keyboard) {
        String input = ui.readLine(keyboard);
        if (input == null) {
            return false;
        }

        Parser.Command command = Parser.parse(input);
        switch (command.getType()) {
        case EMPTY:
            ui.showEmptyInput();
            return true;
        case BYE:
            bye();
            return false;
        case LIST:
            list();
            return true;
        case MARK:
            mark(command.getArgument());
            return true;
        case UNMARK:
            unmark(command.getArgument());
            return true;
        case DELETE:
            delete(command.getArgument());
            return true;
        case TIMECHECK:
            timecheck(command.getArgument());
            return true;
        case TODO:
            add(TaskType.TODO, command.getArgument());
            return true;
        case DEADLINE:
            addDeadline(command.getArgument());
            return true;
        case EVENT:
            addEvent(command.getArgument());
            return true;
        case UNKNOWN:
        default:
            ui.showUnknownCommand();
            return true;
        }
    }

    public static void main(String[] args) {
        //Turtley ASCII art was by me.
        ui.showWelcome();

        try {
            taskList.addAll(Storage.load());
        } catch (TurtleyException exception) {
            ui.showError(exception);
        }

        //running variable is true when the application is running
        boolean running = true;

        //Initialise Scanner of user inputs
        Scanner keyboard = new Scanner(System.in);

        //Keep prompting the user while app is running
        while (running) {
            running = prompt(keyboard);
        }
        //Close the Scanner once done
        keyboard.close();
    }
}
