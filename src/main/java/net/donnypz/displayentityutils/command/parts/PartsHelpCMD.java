package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.command.ConsoleUsableSubCommand;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

class PartsHelpCMD extends ConsoleUsableSubCommand {

    PartsHelpCMD() {
        super(Permission.HELP, false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
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
