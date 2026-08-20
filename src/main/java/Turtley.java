import java.util.ArrayList;
import java.util.Scanner;

public class Turtley {

    private static final String SEPARATOR = "____________________________________________________________";
    private static final int MAX_TASK_NUM = 100;
    private static final ArrayList<Task> taskList = new ArrayList<>();

    //Adds a task object to the task list.
    public static void add(Task newTask) {
        if (taskList.size() >= MAX_TASK_NUM) {
            System.out.println(SEPARATOR);
            System.out.println("Task list full, do some work you lazy bum! o/T\\>");
            System.out.println(SEPARATOR);
            return;
        }
        taskList.add(newTask);
        System.out.println(SEPARATOR);
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + newTask);
        System.out.println("Now you have " + taskList.size() + " tasks in the list.");
        System.out.println(SEPARATOR);
    }

    //Retains the earlier task creation interface for ordinary task types.
    public static void add(String taskType, String input) {
        if ("T".equals(taskType)) {
            try {
                add(new ToDo(input));
            } catch (TurtleyException exception) {
                showError(exception);
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
        add(new Deadline(description, by));
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
        add(new Event(description, from, to));
    }

    //Prints a helpful message when a structured task command is malformed.(Written by ChatGPT)
    private static void showInvalidTaskFormat(String format) {
        showError(new TurtleyException("Invalid format. Use: " + format));
    }

    //Prints a user-facing error while keeping the Turtley prompt suffix consistent.
    private static void showError(TurtleyException exception) {
        System.out.println(SEPARATOR);
        System.out.println(" " + exception.getMessage() + " o/T\\>");
        System.out.println(SEPARATOR);
    }

    //Reads from the taskList
    public static void list() {
        if (taskList.isEmpty()) {
            System.out.println(SEPARATOR);
            System.out.println("Task list empty. Good job! Here's a cookie. o/T\\>");
            System.out.println(SEPARATOR);
            return;
        }

        //Print out list display
        System.out.println(SEPARATOR);
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < taskList.size(); i++) {
            System.out.println(" " + (i + 1) + "." + taskList.get(i));
        }
        System.out.println(SEPARATOR);
    }

    //Marks the task at the given one-based list index as done. (Written by ChatGPT)
    public static void mark(String input) {
        try {
            int taskIndex = parseTaskIndex(input);
            if (taskIndex < 0 || taskIndex >= taskList.size()) {
                throw new TurtleyException("Task number is not in your list.");
            }

            taskList.get(taskIndex).markAsDone();
            System.out.println(SEPARATOR);
            System.out.println(" Nice! I've marked this task as done:");
            System.out.println("   [" + taskList.get(taskIndex).getStatusIcon() + "] "
                    + taskList.get(taskIndex).getDescription());
            System.out.println(SEPARATOR);
        } catch (TurtleyException exception) {
            showError(exception);
        }
    }

    //Marks the task at the given one-based list index as not done. (Written by ChatGPT)
    public static void unmark(String input) {
        try {
            int taskIndex = parseTaskIndex(input);
            if (taskIndex < 0 || taskIndex >= taskList.size()) {
                throw new TurtleyException("Task number is not in your list.");
            }

            taskList.get(taskIndex).markAsNotDone();
            System.out.println(SEPARATOR);
            System.out.println(" OK, I've marked this task as not done yet:");
            System.out.println("   [" + taskList.get(taskIndex).getStatusIcon() + "] "
                    + taskList.get(taskIndex).getDescription());
            System.out.println(SEPARATOR);
        } catch (TurtleyException exception) {
            showError(exception);
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
            System.out.println(SEPARATOR);
            System.out.println(" Noted. I've removed this task:");
            System.out.println("   " + deletedTask);
            System.out.println(" Now you have " + taskList.size() + " tasks in the list.");
            System.out.println(SEPARATOR);
        } catch (TurtleyException exception) {
            showError(exception);
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
        System.out.println(SEPARATOR);
        System.out.println("Bye. See you around! o/T\\>");
        System.out.println(SEPARATOR);
    }

    //Waits for input from the user
    public static boolean prompt(Scanner keyboard) {
        //Line 21-24 written by ChatGPT (I genuinely think this is unnecessary though)
        if (!keyboard.hasNextLine()) {
            return false;
        }
        String input = keyboard.nextLine();
        switch (input) {
            case "" -> {System.out.println("Please input something. o/T\\>");
                        System.out.println(SEPARATOR);
                        return true;}
            case "bye" -> {bye(); return false;}
            case "list" -> {list(); return true;}
            default -> {
                if (input.startsWith("mark ")) { //(Written by ChatGPT)
                    mark(input.substring(5).trim());
                } else if (input.startsWith("unmark ")) { //(Written by ChatGPT)
                    unmark(input.substring(7).trim());
                } else if (input.equals("delete") || input.startsWith("delete ")) {
                    delete(input.length() == 6 ? "" : input.substring(7).trim());
                } else if (input.equals("todo") || input.startsWith("todo ")) {
                    String description = input.length() == 4 ? "" : input.substring(5).trim();
                    add("T", description);
                } else if (input.startsWith("deadline ")) {
                    addDeadline(input.substring(9).trim());
                } else if (input.startsWith("event ")) {
                    addEvent(input.substring(6).trim());
                } else {
                    System.out.println("Please input something correct. o/T\\>");
                    System.out.println(SEPARATOR);
                }
                return true;
            }
        }
    }

    public static void main(String[] args) {
        //Turtley ASCII art was by me.
        String banner = "      _____________ \n"
                + "__   /__|_______|__\\ \n"
                + "\\^ \\/______|_|______\\\n"
                + " \\ /_______|_|_______\\>\n"
                + "   |_/ |_/     \\_| \\_|\n Turtley";

        //Line 11-17 was written by ChatGPT.
        System.out.println(SEPARATOR);
        System.out.println(banner);
        System.out.println("Hello! I'm Turtley.");
        System.out.println("What can I do for you? o/T\\>");
        System.out.println(SEPARATOR);

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
