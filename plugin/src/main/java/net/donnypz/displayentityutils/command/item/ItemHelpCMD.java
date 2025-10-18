package net.donnypz.displayentityutils.command.item;

import net.donnypz.displayentityutils.command.ConsoleUsableSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import org.bukkit.command.CommandSender;

public class ItemHelpCMD extends ConsoleUsableSubCommand {
    public ItemHelpCMD() {
        super(Permission.HELP);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 3){
            ItemCMD.help(sender, 1);
        }
        else{
            try{
                ItemCMD.help(sender, Integer.parseInt(args[2]));
            }
            catch(NumberFormatException e){
                ItemCMD.help(sender, 1);
            }
        }
    }
}
