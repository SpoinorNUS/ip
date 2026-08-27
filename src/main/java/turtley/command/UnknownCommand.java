package turtley.command;

import turtley.model.TaskList;
import turtley.storage.Storage;
import turtley.ui.Ui;

/**
 * Responds to an unrecognized input line.
 */
public class UnknownCommand extends Command {

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showUnknownCommand();
    }
}
