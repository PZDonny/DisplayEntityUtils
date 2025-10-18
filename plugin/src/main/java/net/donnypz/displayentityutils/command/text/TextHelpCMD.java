package net.donnypz.displayentityutils.command.text;

import net.donnypz.displayentityutils.command.ConsoleUsableSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import org.bukkit.command.CommandSender;

public class TextHelpCMD extends ConsoleUsableSubCommand {
    public TextHelpCMD() {
        super(Permission.HELP);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 3){
            TextCMD.help(sender, 1);
        }
        else{
            try{
                TextCMD.help(sender, Integer.parseInt(args[2]));
            }
            catch(NumberFormatException e){
                TextCMD.help(sender, 1);
            }
        }
    }
}
