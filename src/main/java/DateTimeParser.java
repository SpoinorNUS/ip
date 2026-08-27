import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.Temporal;
import java.util.List;

//(Written by ChatGPT)
/**
 * Parses and formats the date and date-time values accepted by Turtley.
 */
public final class DateTimeParser {

    /** The formats shown to users when a date/time value is invalid. */
    private static final String SUPPORTED_FORMATS =
            "yyyy-MM-dd, dd-MM-yyyy, yyyy-MM-dd HH:mm, dd-MM-yyyy HH:mm, yyyy-MM-dd HHmm, or dd-MM-yyyy HHmm";

    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = List.of(
            formatter("uuuu-MM-dd HH:mm"),
            formatter("dd-MM-uuuu HH:mm"),
            formatter("uuuu-MM-dd HHmm"),
            formatter("dd-MM-uuuu HHmm"),
            formatter("uuuu-MM-dd'T'HH:mm"),
            formatter("dd-MM-uuuu'T'HH:mm"));

    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
            formatter("uuuu-MM-dd"),
            formatter("dd-MM-uuuu"));

    private DateTimeParser() {
        // Utility class; do not create instances.
    }

    /**
     * Parses a date or date-time value into the most specific Java time type.
     *
     * @param value the user-entered value
     * @return a {@link LocalDateTime} for values containing a time, otherwise a {@link LocalDate}
     * @throws TurtleyException if the value is blank or does not use a supported format
     */
    public static Temporal parse(String value) {
        if (value == null || value.isBlank()) {
            throw invalidDateTime();
        }

        String trimmedValue = value.trim();
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(trimmedValue, formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next supported date-time format.
            }
        }
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(trimmedValue, formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next supported date format.
            }
        }
        throw invalidDateTime();
    }

    /**
     * Formats a parsed value in the canonical form used by the UI and save file.
     *
     * @param value the parsed date or date-time value
     * @return the canonical ISO-like representation
     * @throws TurtleyException if the value is not a supported Java time type
     */
    public static String format(Temporal value) {
        if (value instanceof LocalDateTime dateTime) {
            return dateTime.format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm"));
        }
        if (value instanceof LocalDate date) {
            return date.format(DateTimeFormatter.ofPattern("uuuu-MM-dd"));
        }
        throw new TurtleyException("Unable to format an unsupported date/time value.");
    }

    /**
     * Creates the consistent user-facing parsing error.
     *
     * @return the parsing error
     */
    private static TurtleyException invalidDateTime() {
        return new TurtleyException("Invalid date/time format. Use " + SUPPORTED_FORMATS + ".");
    }

    /**
     * Builds a strict formatter so invalid calendar dates are rejected.
     *
     * @param pattern the formatter pattern
     * @return a strict formatter for the pattern
     */
    private static DateTimeFormatter formatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern).withResolverStyle(ResolverStyle.STRICT);
    }
}
