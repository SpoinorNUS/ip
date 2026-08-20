import java.util.Scanner;

public class Turtley {

    private static final String SEPARATOR = "____________________________________________________________";
    private static final int MAX_TASK_NUM = 100;
    private static final String[] taskList = new String[MAX_TASK_NUM];
    private static int numOfTasks = 0;

    //When user inputs any non-commands, add the exact words as a task to task list
    public static void add(String input) {
        if (numOfTasks >= MAX_TASK_NUM) {
            System.out.println(SEPARATOR);
            System.out.println("Task list full, do some work you lazy bum!");
            System.out.println(SEPARATOR);
            return;
        }
        taskList[numOfTasks] = input;
        numOfTasks++;
        System.out.println(SEPARATOR);
        System.out.println("added: " + input);
        System.out.println(SEPARATOR);
    }

    //Reads from the taskList
    public static void list() {
        if (numOfTasks <= 0) {
            System.out.println(SEPARATOR);
            System.out.println("Task list empty. Good job! Here's a cookie.");
            System.out.println(SEPARATOR);
            return;
        }

        //Print out list display
        System.out.println(SEPARATOR);
        for (int i = 0; i < taskList.length; i++) {
            if (i >= numOfTasks) {
                break;
            }
            System.out.println((i+1)+". " + taskList[i]);
        }
        System.out.println(SEPARATOR);
    }

    //Terminates the app
    public static void bye() {
        System.out.println(SEPARATOR);
        System.out.println("Bye. See you around!");
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
            case "" -> {System.out.println("Please input something."); return true;}
            case "bye" -> {bye(); return false;}
            case "list" -> {list(); return true;}
            default -> {add(input); return true;}
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
        System.out.println("What can I do for you?");
        System.out.println(SEPARATOR);

        //running variable is true when the application is running
        boolean running = true;

        //Initialise Scanner of user inputs
        Scanner keyboard = new Scanner(System.in);

        //Keep prompting the user while app is running
        while (running) {
            running = prompt(keyboard);
        }
    }
}
