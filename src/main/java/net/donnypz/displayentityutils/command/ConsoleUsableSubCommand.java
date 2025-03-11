package net.donnypz.displayentityutils.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

abstract class ConsoleUsableSubCommand extends SubCommand {
    ConsoleUsableSubCommand(@NotNull Permission permission) {
        super(permission);
    }

    abstract void execute(CommandSender sender, String[] args);
}
