package net.donnypz.displayentityutils.command.bdengine;

import net.donnypz.displayentityutils.command.ConsoleUsableSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import org.bukkit.command.CommandSender;

public class BDEngineHelpCMD extends ConsoleUsableSubCommand {

    public BDEngineHelpCMD() {
        super(Permission.HELP);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 3){
            BDEngineCMD.help(sender, 1);
        }
        else{
            try{
                BDEngineCMD.help(sender, Integer.parseInt(args[2]));
            }
            catch(NumberFormatException e){
                BDEngineCMD.help(sender, 1);
            }
        }
    }
}
