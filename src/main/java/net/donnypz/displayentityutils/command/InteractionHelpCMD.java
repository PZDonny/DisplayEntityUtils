package net.donnypz.displayentityutils.command;

import org.bukkit.command.CommandSender;

class InteractionHelpCMD extends ConsoleUsableSubCommand {

    InteractionHelpCMD() {
        super(Permission.HELP);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 3){
            InteractionCMD.interactionHelp(sender, 1);
        }
        else{
            try{
                InteractionCMD.interactionHelp(sender, Integer.parseInt(args[2]));
            }
            catch(NumberFormatException e){
                InteractionCMD.interactionHelp(sender, 1);
            }
        }
    }

}
