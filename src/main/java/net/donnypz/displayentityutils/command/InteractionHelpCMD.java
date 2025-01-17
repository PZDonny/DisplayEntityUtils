package net.donnypz.displayentityutils.command;

import org.bukkit.entity.Player;

class InteractionHelpCMD implements SubCommand{

    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.HELP)){
            return;
        }

        if (args.length < 3){
            InteractionCMD.interactionHelp(player, 1);
        }
        else{
            try{
                InteractionCMD.interactionHelp(player, Integer.parseInt(args[2]));
            }
            catch(NumberFormatException e){
                InteractionCMD.interactionHelp(player, 1);
            }
        }
    }

}
