import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;

//(Written by ChatGPT)
/**
 * Saves Turtley's task list to the local file system.
 */
public class Storage {

    private static final Path DATA_FILE = Path.of("data", "turtley.txt");
    private static final Path TEMP_DATA_FILE = Path.of("data", "turtley.txt.tmp");
    private static final int MAX_TASK_NUM = 100;

    private Storage() {
        // Utility class; do not create instances.
    }

    /**
     * Replaces the saved task list with the supplied tasks.
     *
     * @param tasks the current task list
     * @throws TurtleyException if the task list is invalid or cannot be written
     */
    public static void save(List<Task> tasks) {
        if (tasks == null) {
            throw new TurtleyException("Unable to save tasks: task list is null.");
        }
        if (tasks.size() > MAX_TASK_NUM) {
            throw new TurtleyException("Unable to save tasks: task list exceeds 100 tasks.");
        }

        StringBuilder fileContents = new StringBuilder();
        for (Task task : tasks) {
            if (fileContents.length() > 0) {
                fileContents.append(System.lineSeparator());
            }
            fileContents.append(serialize(task));
        }

        try {
            Files.createDirectories(DATA_FILE.getParent());
            if (tasks.isEmpty()) {
                Files.deleteIfExists(DATA_FILE);
                return;
            }
            Files.writeString(TEMP_DATA_FILE, fileContents.toString(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            Files.move(TEMP_DATA_FILE, DATA_FILE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | SecurityException exception) {
            throw new TurtleyException("Unable to save tasks to disk.", exception);
        } finally {
            try {
                Files.deleteIfExists(TEMP_DATA_FILE);
            } catch (IOException | SecurityException ignored) {
                // The next save overwrites the temporary file if cleanup is unavailable.
            }
        }
    }

    /**
     * Converts a task into the line format used by the save file.
     *
     * @param task the task to serialize
     * @return one save-file line for the task
     */
    private static String serialize(Task task) {
        if (task == null) {
            throw new TurtleyException("Unable to save tasks: task list contains a null task.");
        }

        String description = requireField(task.getDescription(), "description");
        TaskType taskType = task.getTaskType();
        if (taskType == null) {
            throw new TurtleyException("Unable to save tasks: task type is invalid.");
        }
        StringBuilder line = new StringBuilder()
                .append(task.getTypeIcon())
                .append(" | ")
                .append(task.isDone() ? "1" : "0")
                .append(" | ")
                .append(escape(description));

        switch (taskType) {
        case TODO:
            break;
        case DEADLINE:
            if (!(task instanceof Deadline deadline)) {
                throw new TurtleyException("Unable to save tasks: deadline has no deadline value.");
            }
            line.append(" | ").append(escape(requireField(deadline.getBy(), "deadline")));
            break;
        case EVENT:
            if (!(task instanceof Event event)) {
                throw new TurtleyException("Unable to save tasks: event has no time range.");
            }
            line.append(" | ").append(escape(requireField(event.getFrom(), "start time")))
                    .append(" | ").append(escape(requireField(event.getTo(), "end time")));
            break;
        default:
            throw new TurtleyException("Unable to save tasks: task type is invalid.");
        }

        return line.toString();
    }

    /**
     * Escapes characters that have a special meaning in the save-file format.
     *
     * @param value the field value
     * @return the escaped field value
     */
    private static String escape(String value) {
        return value.replace("\\", "\\\\")
                .replace("|", "\\|")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }

    /**
     * Ensures that a required save-file field is present and non-blank.
     *
     * @param value the field value
     * @param fieldName the field's human-readable name
     * @return the valid field value
     */
    private static String requireField(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new TurtleyException("Unable to save tasks: " + fieldName + " is blank.");
        }
        return value;
    }
}