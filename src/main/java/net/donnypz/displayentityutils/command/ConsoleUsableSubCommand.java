package net.donnypz.displayentityutils.command;

import org.bukkit.command.CommandSender;

interface ConsoleUsableSubCommand extends SubCommand {
    void execute(CommandSender sender, String[] args);
}
