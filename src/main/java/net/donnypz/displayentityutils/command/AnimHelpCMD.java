package net.donnypz.displayentityutils.command;

import org.bukkit.command.CommandSender;

class AnimHelpCMD implements ConsoleUsableSubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(sender, Permission.HELP)){
            return;
        }

        if (args.length < 3){
            AnimCMD.animationHelp(sender, 1);
        }
        else{
            try{
                AnimCMD.animationHelp(sender, Integer.parseInt(args[2]));
            }
            catch(NumberFormatException e){
                AnimCMD.animationHelp(sender, 1);
            }
        }
    }

}
