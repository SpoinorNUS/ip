import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    private static final int MAX_TASK_NUM = 100;

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
        try {
            if (Files.notExists(DATA_FILE)) {
                return new ArrayList<>();
            }
            if (!Files.isRegularFile(DATA_FILE)) {
                throw new TurtleyException("Unable to load tasks from disk: save path is not a file.");
            }

            List<Task> tasks = new ArrayList<>();
            try (BufferedReader reader = Files.newBufferedReader(DATA_FILE, StandardCharsets.UTF_8)) {
                String line;
                int lineNumber = 0;
                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    if (line.isBlank()) {
                        continue;
                    }
                    if (tasks.size() >= MAX_TASK_NUM) {
                        throw new TurtleyException("Unable to load tasks from disk: task list exceeds 100 tasks.");
                    }
                    tasks.add(deserialize(line, lineNumber));
                }
            }
            return tasks;
        } catch (IOException | SecurityException exception) {
            throw new TurtleyException("Unable to load tasks from disk.", exception);
        }
    }

    /**
     * Converts one save-file line back into a task.
     *
     * @param line the serialized task line
     * @param lineNumber the line number used in error messages
     * @return the reconstructed task
     * @throws TurtleyException if the line does not match the save format
     */
    private static Task deserialize(String line, int lineNumber) {
        List<String> fields = splitFields(line);
        if (fields.size() < 3) {
            throw invalidLine(lineNumber, line);
        }

        boolean isDone = parseStatus(fields.get(1), lineNumber, line);
        Task task;
        switch (fields.get(0)) {
        case "T":
            if (fields.size() != 3) {
                throw invalidLine(lineNumber, line);
            }
            task = new ToDo(requireField(fields.get(2), "description", lineNumber));
            break;
        case "D":
            if (fields.size() != 4) {
                throw invalidLine(lineNumber, line);
            }
            task = new Deadline(requireField(fields.get(2), "description", lineNumber),
                    requireField(fields.get(3), "deadline", lineNumber));
            break;
        case "E":
            if (fields.size() != 5) {
                throw invalidLine(lineNumber, line);
            }
            task = new Event(requireField(fields.get(2), "description", lineNumber),
                    requireField(fields.get(3), "start time", lineNumber),
                    requireField(fields.get(4), "end time", lineNumber));
            break;
        default:
            throw invalidLine(lineNumber, line);
        }

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Splits a record while respecting escaped separators and special characters.
     *
     * @param line the serialized record
     * @return the decoded fields
     */
    private static List<String> splitFields(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean escaping = false;

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (escaping) {
                switch (character) {
                case '\\', '|' -> field.append(character);
                case 'n' -> field.append('\n');
                case 'r' -> field.append('\r');
                default -> field.append('\\').append(character);
                }
                escaping = false;
            } else if (character == '\\') {
                escaping = true;
            } else if (character == '|') {
                fields.add(field.toString().trim());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }

        if (escaping) {
            field.append('\\');
        }
        fields.add(field.toString().trim());
        return fields;
    }

    /**
     * Parses the completion flag stored in a save-file line.
     *
     * @param status the stored completion flag
     * @param lineNumber the line number used in error messages
     * @param line the complete line
     * @return whether the task is complete
     */
    private static boolean parseStatus(String status, int lineNumber, String line) {
        if ("1".equals(status)) {
            return true;
        }
        if ("0".equals(status)) {
            return false;
        }
        throw invalidLine(lineNumber, line);
    }

    /**
     * Ensures that a required field is present and non-blank.
     *
     * @param value the field value
     * @param fieldName the field's human-readable name
     * @param lineNumber the line number used in error messages
     * @return the valid field value
     */
    private static String requireField(String value, String fieldName, int lineNumber) {
        if (value == null || value.isBlank()) {
            throw new TurtleyException("Invalid task data on line " + lineNumber + ": "
                    + fieldName + " is blank.");
        }
        return value;
    }

    /**
     * Creates a consistent exception for malformed save-file entries.
     *
     * @param lineNumber the malformed line's number
     * @param line the malformed line
     * @return the resulting exception
     */
    private static TurtleyException invalidLine(int lineNumber, String line) {
        return new TurtleyException("Invalid task data on line " + lineNumber + ": " + line);
    }
}