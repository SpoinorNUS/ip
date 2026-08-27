/**
 * Deletes one task.
 */
public class DeleteCommand extends IndexedCommand {

    private final String input;

    /**
     * Creates a delete command.
     *
     * @param input the user-supplied task number
     */
    public DeleteCommand(String input) {
        this.input = input;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        int taskIndex = requireTaskIndex(input, tasks);
        Task deletedTask = tasks.remove(taskIndex);
        saveOrRollback(tasks, storage, () -> tasks.add(taskIndex, deletedTask));
        ui.showTaskDeleted(deletedTask, tasks.size());
    }
}
