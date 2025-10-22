package net.donnypz.displayentityutils.utils;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.ApiStatus;

public class InteractionCommand {
    String command;
    boolean isLeftClick;
    boolean isConsoleCommand;
    NamespacedKey key;

    public InteractionCommand(String command, boolean isLeftClick, boolean isConsoleCommand, NamespacedKey key){
        this.command = command;
        this.isLeftClick = isLeftClick;
        this.isConsoleCommand = isConsoleCommand;
        this.key = key;
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

    @ApiStatus.Internal
    public NamespacedKey getKey(){
        return key;
    }
}
