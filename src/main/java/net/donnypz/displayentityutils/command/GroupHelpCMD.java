package net.donnypz.displayentityutils.command;

import org.bukkit.entity.Player;

class GroupHelpCMD implements SubCommand{

    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.HELP)){
            return;
        }

        if (args.length < 3){
            GroupCMD.groupHelp(player, 1);
        }
        else{
            try{
                GroupCMD.groupHelp(player, Integer.parseInt(args[2]));
            }
            catch(NumberFormatException e){
                GroupCMD.groupHelp(player, 1);
            }
        }
    }

}
