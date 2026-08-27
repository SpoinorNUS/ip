/**
 * Marks one task as not done.
 */
public class UnmarkCommand extends IndexedCommand {

    private final String input;

    /**
     * Creates an unmark command.
     *
     * @param input the user-supplied task number
     */
    public UnmarkCommand(String input) {
        this.input = input;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        int taskIndex = requireTaskIndex(input, tasks);
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        task.markAsNotDone();
        saveOrRollback(tasks, storage, () -> {
            if (wasDone) {
                task.markAsDone();
            }
        });
        ui.showTaskUnmarked(task);
    }
}
