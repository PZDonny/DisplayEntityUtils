package net.donnypz.displayentityutils.command;

import org.bukkit.entity.Player;

class ItemHelpCMD implements SubCommand{

    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.HELP)){
            return;
        }
        ItemCMD.itemHelp(player);
    }

}
