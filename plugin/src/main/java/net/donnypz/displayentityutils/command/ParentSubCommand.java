package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayAPI;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class ParentSubCommand extends ConsoleUsableSubCommand{

    String[] commands = null;

    public ParentSubCommand(@NotNull String commandName) {
        super(commandName, Permission.HELP);
        new HelpCMD(this);
        setOptionalTabComplete(2, "[page-number]");
    }

    @Override
    public final void execute(CommandSender sender, String[] args) {
        if (args.length < 2){
            help(sender, 1);
            return;
        }
        String arg = args[1];
        DEUSubCommand subCommand = subCommands.get(arg);
        if (subCommand == null){
            help(sender, 1);
        }
        else{
            DisplayEntityPluginCommand.executeCommand(subCommand, sender, args);
        }
    }

    void help(CommandSender sender, int page){
        if (commands == null) commands = subCommands.sequencedKeySet().toArray(new String[0]);
        int maxPageCount = (int) Math.ceil(subCommands.size() / 7.0);
        page = Math.min(Math.max(page, 1), maxPageCount);
        int max = Math.min(page*7, commands.length-1);

        sender.sendMessage(DisplayAPI.pluginPrefixLong);
        for (int i = max-7; i <= max; i++){
            String cmdName = commands[i];
            DEUSubCommand subCmd = subCommands.get(cmdName);
            CMDUtils.sendCMD(sender, subCmd.getShortCommandUsage());
            //CMDUtils.sendCMD(sender, subCmd.getShortCommandUsage(), subCmd.getDescription());
        }
    }

    class HelpCMD extends ConsoleUsableSubCommand{
        HelpCMD(@NotNull DEUSubCommand parentSubCommand) {
            super("help", parentSubCommand, Permission.HELP);
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            if (args.length < 3){
                help(sender, 1);
            }
            else{
                try{
                    help(sender, Integer.parseInt(args[2]));
                }
                catch(NumberFormatException e){
                    help(sender, 1);
                }
            }
        }
    }
}
