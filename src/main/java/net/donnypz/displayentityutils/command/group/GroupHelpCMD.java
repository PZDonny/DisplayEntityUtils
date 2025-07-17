package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.command.ConsoleUsableSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import org.bukkit.command.CommandSender;

class GroupHelpCMD extends ConsoleUsableSubCommand {

    GroupHelpCMD() {
        super(Permission.HELP);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

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
