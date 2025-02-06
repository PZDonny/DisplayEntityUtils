package net.donnypz.displayentityutils.command;

import org.bukkit.command.CommandSender;

class PartsHelpCMD implements ConsoleUsableSubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(sender, Permission.HELP)){
            return;
        }

        if (args.length < 3){
            PartsCMD.partsHelp(sender, 1);
        }
        else{
            try{
                PartsCMD.partsHelp(sender, Integer.parseInt(args[2]));
            }
            catch(NumberFormatException e){
                PartsCMD.partsHelp(sender, 1);
            }
        }
    }

}
