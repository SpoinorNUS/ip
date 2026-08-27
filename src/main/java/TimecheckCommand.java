import java.time.temporal.Temporal;

/**
 * Displays deadline and event tasks on or before a supplied date/time.
 */
public class TimecheckCommand extends Command {

    private final String input;

    /**
     * Creates a timecheck command.
     *
     * @param input the cutoff date/time
     */
    public TimecheckCommand(String input) {
        this.input = input;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        Temporal cutoff = DateTimeParser.parse(input);
        String cutoffText = DateTimeParser.format(cutoff);
        boolean hasMatchingTask = false;

        ui.showTimecheckHeader(cutoffText);
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            boolean isDeadline = task instanceof Deadline deadline
                    && DateTimeParser.isOnOrBefore(deadline.getBy(), cutoff);
            boolean isEvent = task instanceof Event event
                    && DateTimeParser.isOnOrBefore(event.getFrom(), cutoff);
            if (isDeadline || isEvent) {
                hasMatchingTask = true;
                ui.showTask(i, task);
            }
        }
        if (!hasMatchingTask) {
            ui.showNoMatchingTasks();
        }
        ui.showSeparator();
    }
}
