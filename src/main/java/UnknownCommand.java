/**
 * Responds to an unrecognized input line.
 */
public class UnknownCommand extends Command {

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showUnknownCommand();
    }
}
