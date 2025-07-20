package net.donnypz.displayentityutils.command.anim;

import net.donnypz.displayentityutils.command.ConsoleUsableSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import org.bukkit.command.CommandSender;

class AnimHelpCMD extends ConsoleUsableSubCommand {

    AnimHelpCMD() {
        super(Permission.HELP, false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 3){
            AnimCMD.animationHelp(sender, 1);
        }
        else{
            try{
                AnimCMD.animationHelp(sender, Integer.parseInt(args[2]));
            }
            catch(NumberFormatException e){
                AnimCMD.animationHelp(sender, 1);
            }
        }
    }

}
