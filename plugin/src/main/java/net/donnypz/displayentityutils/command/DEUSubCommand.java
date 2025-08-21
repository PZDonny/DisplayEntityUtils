package net.donnypz.displayentityutils.command;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public abstract class DEUSubCommand {
    Permission permission;
    protected final HashMap<String, DEUSubCommand> subCommands = new HashMap<>();

    DEUSubCommand(@NotNull Permission permission, boolean hasHelpCommand){
        this.permission = permission;
        if (hasHelpCommand) subCommands.put("help", null);
    }

    DEUSubCommand(@NotNull Permission permission, @NotNull DEUSubCommand helpSubCommand){
        this.permission = permission;
        subCommands.put("help", helpSubCommand);
    }

    DEUSubCommand(String commandName, @NotNull DEUSubCommand parentSubCommand, @NotNull Permission permission){
        parentSubCommand.subCommands.put(commandName, this);
        this.permission = permission;
    }

    public Permission getPermission() {
        return permission;
    }

}
