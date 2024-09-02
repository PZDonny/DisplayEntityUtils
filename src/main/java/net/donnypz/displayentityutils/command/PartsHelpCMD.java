package net.donnypz.displayentityutils.command;

import org.bukkit.entity.Player;

class PartsHelpCMD implements SubCommand{

    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.HELP)){
            return;
        }

        if (args.length < 3){
            PartsCMD.partsHelp(player, 1);
        }
        else{
            try{
                PartsCMD.partsHelp(player, Integer.parseInt(args[2]));
            }
            catch(NumberFormatException e){
                PartsCMD.partsHelp(player, 1);
            }
        }
    }

}
