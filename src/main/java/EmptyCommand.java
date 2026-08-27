/**
 * Responds to an empty input line.
 */
public class EmptyCommand extends Command {

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showEmptyInput();
    }
}
