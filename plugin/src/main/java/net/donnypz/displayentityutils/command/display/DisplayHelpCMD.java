package net.donnypz.displayentityutils.command.display;

import net.donnypz.displayentityutils.command.ConsoleUsableSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.item.ItemCMD;
import org.bukkit.command.CommandSender;

public class DisplayHelpCMD extends ConsoleUsableSubCommand {
    public DisplayHelpCMD() {
        super(Permission.HELP);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 3){
            DisplayCMD.help(sender, 1);
        }
        else{
            try{
                DisplayCMD.help(sender, Integer.parseInt(args[2]));
            }
            catch(NumberFormatException e){
                DisplayCMD.help(sender, 1);
            }
        }
    }
}
