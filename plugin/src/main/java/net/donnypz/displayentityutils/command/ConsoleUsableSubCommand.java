package net.donnypz.displayentityutils.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class ConsoleUsableSubCommand extends DEUSubCommand {
    public ConsoleUsableSubCommand(@NotNull Permission permission) {
        super(permission);
    }

    public ConsoleUsableSubCommand(@NotNull Permission permission, @NotNull DEUSubCommand helpSubCommand) {
        super(permission, helpSubCommand);
    }

    public ConsoleUsableSubCommand(@NotNull String commandName, @NotNull DEUSubCommand parentSubCommand, @NotNull Permission permission) {
        super(commandName, parentSubCommand, permission);
    }

    public abstract void execute(CommandSender sender, String[] args);
}
