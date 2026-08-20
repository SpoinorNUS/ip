import java.util.Scanner;

public class Turtley {

    private static final String SEPARATOR = "____________________________________________________________";
    private static final String[] taskList = new String[100];
    private static int numOfTasks = 0;

    //Line 7-12 was written by ChatGPT
    public static void add(String input) {
        if (numOfTasks >= 100) {
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

    public static void bye() {
        System.out.println(SEPARATOR);
        System.out.println("Bye. See you around!");
        System.out.println(SEPARATOR);
    }

    public static boolean prompt(Scanner keyboard) {
        //Line 21-24 written by ChatGPT (I genuinely think this is unnecessary though)
        if (!keyboard.hasNextLine()) {
            return false;
        }
        String input = keyboard.nextLine();
        //Line 22-27 was written by ChatGPT
        if (input.equals("bye")) {
            bye();
            return false;
        }
        add(input);
        return true;
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
