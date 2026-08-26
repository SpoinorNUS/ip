import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

//(Written by ChatGPT)
/**
 * Loads Turtley's saved task list from the local file system.
 */
public class StorageReader {

    private static final Path DATA_FILE = Path.of("data", "turtley.txt");
    private static final String FIELD_SEPARATOR = "\\s*\\|\\s*";

    private StorageReader() {
        // Utility class; do not create instances.
    }

    /**
     * Reads all saved tasks. A missing save file represents an empty task list.
     *
     * @return the tasks found in the save file
     * @throws TurtleyException if the file cannot be read or contains invalid data
     */
    public static List<Task> load() {
        if (Files.notExists(DATA_FILE)) {
            return new ArrayList<>();
        }

        try {
            List<Task> tasks = new ArrayList<>();
            for (String line : Files.readAllLines(DATA_FILE)) {
                if (!line.isBlank()) {
                    tasks.add(deserialize(line));
                }
            }
            return tasks;
        } catch (IOException exception) {
            throw new TurtleyException("Unable to load tasks from disk.", exception);
        }
    }

    /**
     * Converts one save-file line back into a task.
     *
     * @param line the serialized task line
     * @return the reconstructed task
     * @throws TurtleyException if the line does not match the save format
     */
    private static Task deserialize(String line) {
        String[] fields = line.split(FIELD_SEPARATOR, -1);
        if (fields.length < 3) {
            throw invalidLine(line);
        }

        boolean isDone = parseStatus(fields[1], line);
        Task task;
        switch (fields[0]) {
        case "T":
            if (fields.length != 3) {
                throw invalidLine(line);
            }
            task = new ToDo(fields[2]);
            break;
        case "D":
            if (fields.length != 4) {
                throw invalidLine(line);
            }
            task = new Deadline(fields[2], fields[3]);
            break;
        case "E":
            if (fields.length != 5) {
                throw invalidLine(line);
            }
            task = new Event(fields[2], fields[3], fields[4]);
            break;
        default:
            throw invalidLine(line);
        }

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Parses the completion flag stored in a save-file line.
     *
     * @param status the stored completion flag
     * @param line the complete line, used in the error message
     * @return whether the task is complete
     */
    private static boolean parseStatus(String status, String line) {
        if ("1".equals(status)) {
            return true;
        }
        if ("0".equals(status)) {
            return false;
        }
        throw invalidLine(line);
    }

    /**
     * Creates a consistent exception for malformed save-file entries.
     *
     * @param line the malformed line
     * @return the resulting exception
     */
    private static TurtleyException invalidLine(String line) {
        return new TurtleyException("Invalid task data: " + line);
    }
}
