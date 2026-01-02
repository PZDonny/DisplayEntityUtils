package net.donnypz.displayentityutils.command.place;

import net.donnypz.displayentityutils.command.ConsoleUsableSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.item.ItemCMD;
import org.bukkit.command.CommandSender;

public class PlaceHelpCMD extends ConsoleUsableSubCommand {
    public PlaceHelpCMD() {
        super(Permission.HELP);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 3){
            PlaceCMD.help(sender, 1);
        }
        else{
            try{
                PlaceCMD.help(sender, Integer.parseInt(args[2]));
            }
            catch(NumberFormatException e){
                PlaceCMD.help(sender, 1);
            }
        }
    }
}
