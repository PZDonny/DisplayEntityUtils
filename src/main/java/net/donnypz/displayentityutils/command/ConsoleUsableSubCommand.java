package net.donnypz.displayentityutils.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class ConsoleUsableSubCommand extends DEUSubCommand {
    public ConsoleUsableSubCommand(@NotNull Permission permission) {
        super(permission);
    }

    public abstract void execute(CommandSender sender, String[] args);
}
