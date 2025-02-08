package net.donnypz.displayentityutils.command;

import org.bukkit.command.CommandSender;

class GroupHelpCMD implements ConsoleUsableSubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(sender, Permission.HELP)){
            return;
        }

        if (args.length < 3){
            GroupCMD.groupHelp(sender, 1);
        }
        else{
            try{
                GroupCMD.groupHelp(sender, Integer.parseInt(args[2]));
            }
            catch(NumberFormatException e){
                GroupCMD.groupHelp(sender, 1);
            }
        }
    }

}
