package net.donnypz.displayentityutils.command;

import org.bukkit.command.CommandSender;

class ItemHelpCMD extends ConsoleUsableSubCommand {

    ItemHelpCMD() {
        super(Permission.HELP);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ItemCMD.itemHelp(sender);
    }

}
