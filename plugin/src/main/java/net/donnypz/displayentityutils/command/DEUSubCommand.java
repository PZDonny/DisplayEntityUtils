package net.donnypz.displayentityutils.command;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public abstract class DEUSubCommand {
    Permission permission;
    protected final HashMap<String, DEUSubCommand> subCommands = new HashMap<>();

    DEUSubCommand(@NotNull Permission permission){
        this.permission = permission;
    }

    DEUSubCommand(@NotNull Permission permission, @NotNull DEUSubCommand helpSubCommand){
        this.permission = permission;
        subCommands.put("help", helpSubCommand);
    }

    DEUSubCommand(String commandName, @NotNull DEUSubCommand parentSubCommand, @NotNull Permission permission){
        this.permission = permission;
        parentSubCommand.subCommands.put(commandName, this);

    }

    public Permission getPermission() {
        return permission;
    }

    public boolean isHelpCommand(){
        return permission == Permission.HELP;
    }


}
