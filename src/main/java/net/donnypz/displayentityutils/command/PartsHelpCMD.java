package net.donnypz.displayentityutils.command;

import org.bukkit.command.CommandSender;

class PartsHelpCMD extends ConsoleUsableSubCommand {

    PartsHelpCMD() {
        super(Permission.HELP);
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
