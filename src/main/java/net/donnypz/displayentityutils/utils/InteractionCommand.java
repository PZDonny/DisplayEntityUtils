package net.donnypz.displayentityutils.utils;

public class InteractionCommand {
    String command;
    boolean isLeftClick;
    boolean isConsoleCommand;


    InteractionCommand(String command, boolean isLeftClick, boolean isConsoleCommand){
        this.command = command;
        this.isLeftClick = isLeftClick;
        this.isConsoleCommand = isConsoleCommand;
    }


    public String getCommand() {
        return command;
    }

    public boolean isLeftClick() {
        return isLeftClick;
    }

    public boolean isConsoleCommand() {
        return isConsoleCommand;
    }
}
