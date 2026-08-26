import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

//(Written by ChatGPT)
/**
 * Saves Turtley's task list to the local file system.
 */
public class Storage {

    private static final Path DATA_FILE = Path.of("data", "turtley.txt");

    private Storage() {
        // Utility class; do not create instances.
    }

    /**
     * Replaces the saved task list with the supplied tasks.
     *
     * @param tasks the current task list
     * @throws TurtleyException if the task list cannot be written
     */
    public static void save(List<Task> tasks) {
        StringBuilder fileContents = new StringBuilder();
        for (Task task : tasks) {
            if (fileContents.length() > 0) {
                fileContents.append(System.lineSeparator());
            }
            fileContents.append(serialize(task));
        }

        try {
            Files.createDirectories(DATA_FILE.getParent());
            Files.writeString(DATA_FILE, fileContents.toString());
        } catch (IOException exception) {
            throw new TurtleyException("Unable to save tasks to disk.", exception);
        }
    }

    /**
     * Converts a task into the line format used by the save file.
     *
     * @param task the task to serialize
     * @return one save-file line for the task
     */
    private static String serialize(Task task) {
        StringBuilder line = new StringBuilder()
                .append(task.getTypeIcon())
                .append(" | ")
                .append(task.isDone() ? "1" : "0")
                .append(" | ")
                .append(task.getDescription());

        if (task instanceof Deadline deadline) {
            line.append(" | ").append(deadline.getBy());
        } else if (task instanceof Event event) {
            line.append(" | ").append(event.getFrom())
                    .append(" | ").append(event.getTo());
        }

        return line.toString();
    }
}
