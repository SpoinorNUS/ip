package turtley.command;

import turtley.model.TaskList;
import turtley.storage.Storage;
import turtley.ui.Ui;

/**
 * Responds to an empty input line.
 */
public class EmptyCommand extends Command {

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showEmptyInput();
    }
}
