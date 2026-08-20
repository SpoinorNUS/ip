import java.util.Objects;
import java.util.Scanner;

public class Turtley {

    private static final String SEPARATOR = "____________________________________________________________";

    public static void list() {
        System.out.println(SEPARATOR);
        System.out.println("list");
        System.out.println(SEPARATOR);
    }

    public static void blah() {
        System.out.println(SEPARATOR);
        System.out.println("blah");
        System.out.println(SEPARATOR);
    }

    public static void bye() {
        System.out.println(SEPARATOR);
        System.out.println("Bye. See you around!");
        System.out.println(SEPARATOR);
    }

    public static boolean prompt(Scanner keyboard) {
        String input = keyboard.nextLine();
        if (Objects.equals(input,"list")) {
            list();
            return true;
        } else if (Objects.equals(input, "blah")) {
            blah();
            return true;
        } else if (Objects.equals(input, "bye")) {
            bye();
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        //Turtley ASCII art was by me.
        String banner = """
                      _____________\s
                __   /__|_______|__\\\s
                \\^ \\/______|_|______\\
                 \\ /_______|_|_______\\>
                   |_/ |_/     \\_| \\_|
                 Turtley""";

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
