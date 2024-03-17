package auction.presentation.commands.invoker;

import auction.presentation.interfaces.ICommand;

public class CommandInvoker {
    private ICommand command;

    public void setCommand(ICommand command) {
        this.command = command;
    }

    public void invoke() {
        command.execute();
    }
}
