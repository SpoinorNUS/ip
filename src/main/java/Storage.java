import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

//(Written by ChatGPT)
/**
 * Loads and saves Turtley's task list to the local file system.
 */
public class Storage {

    private final Path dataFile;
    private final Path tempDataFile;
    private static final int MAX_TASK_NUM = 100;

    /**
     * Creates a storage service for the supplied save-file path.
     *
     * @param filePath the save-file path
     */
    public Storage(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new TurtleyException("Unable to use an empty save-file path.");
        }
        dataFile = Path.of(filePath);
        tempDataFile = Path.of(filePath + ".tmp");
    }

    /**
     * Replaces the saved task list with the supplied tasks.
     *
     * @param tasks the current task list
     * @throws TurtleyException if the task list is invalid or cannot be written
     */
    public void save(List<Task> tasks) {
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
            Path parent = dataFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (tasks.isEmpty()) {
                Files.deleteIfExists(dataFile);
                return;
            }
            Files.writeString(tempDataFile, fileContents.toString(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            Files.move(tempDataFile, dataFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | SecurityException exception) {
            throw new TurtleyException("Unable to save tasks to disk.", exception);
        } finally {
            try {
                Files.deleteIfExists(tempDataFile);
            } catch (IOException | SecurityException ignored) {
                // The next save overwrites the temporary file if cleanup is unavailable.
            }
        }
    }


    /**
     * Reads all saved tasks. A missing save file represents an empty task list.
     *
     * @return the tasks found in the save file
     * @throws TurtleyException if the file cannot be read or contains invalid data
     */
    public List<Task> load() {
        try {
            if (Files.notExists(dataFile)) {
                return new ArrayList<>();
            }
            if (!Files.isRegularFile(dataFile)) {
                throw new TurtleyException("Unable to load tasks from disk: save path is not a file.");
            }

            List<Task> tasks = new ArrayList<>();
            try (BufferedReader reader = Files.newBufferedReader(dataFile, StandardCharsets.UTF_8)) {
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
            task = new ToDo(requireLoadedField(fields.get(2), "description", lineNumber));
            break;
        case "D":
            if (fields.size() != 4) {
                throw invalidLine(lineNumber, line);
            }
            task = new Deadline(requireLoadedField(fields.get(2), "description", lineNumber),
                    parseDateTime(fields.get(3), "deadline", lineNumber));
            break;
        case "E":
            if (fields.size() != 5) {
                throw invalidLine(lineNumber, line);
            }
            task = new Event(requireLoadedField(fields.get(2), "description", lineNumber),
                    parseDateTime(fields.get(3), "start time", lineNumber),
                    parseDateTime(fields.get(4), "end time", lineNumber));
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
     * Ensures that a required field loaded from disk is present and non-blank.
     *
     * @param value the field value
     * @param fieldName the field's human-readable name
     * @param lineNumber the line number used in error messages
     * @return the valid field value
     */
    private static String requireLoadedField(String value, String fieldName, int lineNumber) {
        if (value == null || value.isBlank()) {
            throw new TurtleyException("Invalid task data on line " + lineNumber + ": "
                    + fieldName + " is blank.");
        }
        return value;
    }

    /**
     * Parses a date/time field loaded from disk and adds line context to failures.
     *
     * @param value the stored date/time text
     * @param fieldName the field's human-readable name
     * @param lineNumber the line number used in error messages
     * @return the parsed date or date-time
     */
    private static java.time.temporal.Temporal parseDateTime(String value, String fieldName, int lineNumber) {
        try {
            return DateTimeParser.parse(requireLoadedField(value, fieldName, lineNumber));
        } catch (TurtleyException exception) {
            throw new TurtleyException("Invalid task data on line " + lineNumber + ": "
                    + fieldName + " has an invalid date/time format.", exception);
        }
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
            line.append(" | ").append(escape(DateTimeParser.format(deadline.getBy())));
            break;
        case EVENT:
            if (!(task instanceof Event event)) {
                throw new TurtleyException("Unable to save tasks: event has no time range.");
            }
            line.append(" | ").append(escape(DateTimeParser.format(event.getFrom())))
                    .append(" | ").append(escape(DateTimeParser.format(event.getTo())));
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
