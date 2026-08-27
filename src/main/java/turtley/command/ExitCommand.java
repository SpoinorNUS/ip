package turtley.command;

import turtley.model.TaskList;
import turtley.storage.Storage;
import turtley.ui.Ui;

/**
 * Terminates the application.
 */
public class ExitCommand extends Command {

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
