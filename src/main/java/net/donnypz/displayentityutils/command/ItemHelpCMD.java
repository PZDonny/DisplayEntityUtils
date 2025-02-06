package net.donnypz.displayentityutils.command;

import org.bukkit.command.CommandSender;

class ItemHelpCMD implements ConsoleUsableSubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(sender, Permission.HELP)){
            return;
        }
        ItemCMD.itemHelp(sender);
    }

}
