package net.donnypz.displayentityutils.command;

import org.bukkit.entity.Player;

class AnimHelpCMD implements SubCommand{

    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.HELP)){
            return;
        }

        if (args.length < 3){
            AnimCMD.animationHelp(player, 1);
        }
        else{
            try{
                AnimCMD.animationHelp(player, Integer.parseInt(args[2]));
            }
            catch(NumberFormatException e){
                AnimCMD.animationHelp(player, 1);
            }
        }
    }

}
