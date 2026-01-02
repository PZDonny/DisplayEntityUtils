package net.donnypz.displayentityutils.command.mannequin;

import net.donnypz.displayentityutils.command.ConsoleUsableSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import org.bukkit.command.CommandSender;

public class MannequinHelpCMD extends ConsoleUsableSubCommand {
    public MannequinHelpCMD() {
        super(Permission.HELP);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 3){
            MannequinCMD.help(sender, 1);
        }
        else{
            try{
                MannequinCMD.help(sender, Integer.parseInt(args[2]));
            }
            catch(NumberFormatException e){
                MannequinCMD.help(sender, 1);
            }
        }
    }
}