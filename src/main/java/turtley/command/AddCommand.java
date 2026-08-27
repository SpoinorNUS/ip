package turtley.command;

import turtley.model.Task;
import turtley.model.TaskList;
import turtley.storage.Storage;
import turtley.ui.Ui;

/**
 * Base class for commands that create and save one new task.
 */
public abstract class AddCommand extends Command {

    /**
     * Creates the task represented by this command.
     *
     * @return the new task
     */
    protected abstract Task createTask();

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (tasks.isFull()) {
            ui.showTaskListFull();
            return;
        }

        Task task = createTask();
        tasks.add(task);
        saveOrRollback(tasks, storage, () -> tasks.remove(tasks.size() - 1));
        ui.showTaskAdded(task, tasks.size());
    }
}
