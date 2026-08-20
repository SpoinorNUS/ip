import java.util.Scanner;

public class Turtley {

    private static final String SEPARATOR = "____________________________________________________________";
    private static final int MAX_TASK_NUM = 100;
    private static final Task[] taskList = new Task[MAX_TASK_NUM];
    private static int numOfTasks = 0;

    //Adds a task object to the task list.
    public static void add(Task newTask) {
        if (numOfTasks >= MAX_TASK_NUM) {
            System.out.println(SEPARATOR);
            System.out.println("Task list full, do some work you lazy bum! o/T\\>");
            System.out.println(SEPARATOR);
            return;
        }
        taskList[numOfTasks] = newTask;
        numOfTasks++;
        System.out.println(SEPARATOR);
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + newTask);
        System.out.println("Now you have " + numOfTasks + " tasks in the list.");
        System.out.println(SEPARATOR);
    }

    //Retains the earlier task creation interface for ordinary task types.
    public static void add(String taskType, String input) {
        if ("T".equals(taskType)) {
            add(new ToDo(input));
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
        System.out.println(SEPARATOR);
        System.out.println(" Invalid format. Use: " + format);
        System.out.println(SEPARATOR);
    }

    //Reads from the taskList
    public static void list() {
        if (numOfTasks <= 0) {
            System.out.println(SEPARATOR);
            System.out.println("Task list empty. Good job! Here's a cookie. o/T\\>");
            System.out.println(SEPARATOR);
            return;
        }

        //Print out list display
        System.out.println(SEPARATOR);
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < taskList.length; i++) {
            if (i >= numOfTasks) {
                break;
            }
            System.out.println(" " + (i + 1) + "." + taskList[i]);
        }
        System.out.println(SEPARATOR);
    }

    //Marks the task at the given one-based list index as done. (Written by ChatGPT)
    public static void mark(String input) {
        try {
            int taskIndex = Integer.parseInt(input) - 1;
            if (taskIndex < 0 || taskIndex >= numOfTasks) {
                System.out.println(SEPARATOR);
                System.out.println(" Task number is not in your list.");
                System.out.println(SEPARATOR);
                return;
            }

            taskList[taskIndex].markAsDone();
            System.out.println(SEPARATOR);
            System.out.println(" Nice! I've marked this task as done:");
            System.out.println("   [" + taskList[taskIndex].getStatusIcon() + "] "
                    + taskList[taskIndex].getDescription());
            System.out.println(SEPARATOR);
        } catch (NumberFormatException exception) {
            System.out.println(SEPARATOR);
            System.out.println(" Please provide a valid task number.");
            System.out.println(SEPARATOR);
        }
    }

    //Marks the task at the given one-based list index as not done. (Written by ChatGPT)
    public static void unmark(String input) {
        try {
            int taskIndex = Integer.parseInt(input) - 1;
            if (taskIndex < 0 || taskIndex >= numOfTasks) {
                System.out.println(SEPARATOR);
                System.out.println(" Task number is not in your list.");
                System.out.println(SEPARATOR);
                return;
            }

            taskList[taskIndex].markAsNotDone();
            System.out.println(SEPARATOR);
            System.out.println(" OK, I've marked this task as not done yet:");
            System.out.println("   [" + taskList[taskIndex].getStatusIcon() + "] "
                    + taskList[taskIndex].getDescription());
            System.out.println(SEPARATOR);
        } catch (NumberFormatException exception) {
            System.out.println(SEPARATOR);
            System.out.println(" Please provide a valid task number.");
            System.out.println(SEPARATOR);
        }
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
                } else if (input.startsWith("todo ")) {
                    add("T",input.substring(5).trim());
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
