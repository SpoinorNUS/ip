import java.util.Scanner;

/**
 * Coordinates Turtley's application components.
 */
public class Turtley {

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /**
     * Creates Turtley with the default save-file location.
     */
    public Turtley() {
        this("data/turtley.txt");
    }

    /**
     * Creates Turtley with a supplied save-file location.
     *
     * @param filePath the save-file path
     */
    public Turtley(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (TurtleyException exception) {
            ui.showError(exception);
            loadedTasks = new TaskList();
        }
        tasks = loadedTasks;
    }

    /**
     * Runs the command loop until the user exits or input ends.
     */
    public void run() {
        ui.showWelcome();
        boolean running = true;
        Scanner keyboard = new Scanner(System.in);
        while (running) {
            String input = ui.readLine(keyboard);
            if (input == null) {
                break;
            }

            Command command = Parser.parse(input);
            try {
                command.execute(tasks, ui, storage);
            } catch (TurtleyException exception) {
                ui.showError(exception);
            }
            running = !command.isExit();
        }
        keyboard.close();
    }

    public static void main(String[] args) {
        new Turtley().run();
    }
}
