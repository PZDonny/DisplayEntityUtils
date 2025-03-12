package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;

class TextCMD extends ConsoleUsableSubCommand {

    private static final HashMap<String, PlayerSubCommand> subCommands = new HashMap<>();


    TextCMD(){
        super(Permission.HELP);
        subCommands.put("help", null);
        subCommands.put("set", new TextSetCMD());
        subCommands.put("font", new TextFontCMD());
        subCommands.put("shadow", new TextShadowCMD());
        subCommands.put("seethrough", new TextSeeThroughCMD());
        subCommands.put("align", new TextAlignCMD());
        subCommands.put("linewidth", new TextLineWidthCMD());
        subCommands.put("background", new TextBackgroundCMD());
        subCommands.put("opacity", new TextOpacityCMD());
    }

    static List<String> getTabComplete(){
        return subCommands.keySet().stream().toList();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2){
            textHelp(sender);
            return;
        }
        String arg = args[1];
        SubCommand subCommand = subCommands.get(arg);
        if (subCommand == null){
            textHelp(sender);
        }
        else{
            DisplayEntityPluginCommand.executeCommand(subCommand, sender, args);
        }
    }

    static void textHelp(CommandSender sender){
        sender.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
        CMDUtils.sendCMD(sender, "/mdis text help", " (Get help for text displays)");
        CMDUtils.sendCMD(sender,"/mdis text set <text>", " (Set this text for your selected text display)");
        CMDUtils.sendCMD(sender,"/mdis text font <default | alt | uniform | illageralt>", " (Set the text font for your selected text display)");
        CMDUtils.sendCMD(sender,"/mdis text shadow", " (Toggle shadows visibility in your selected text display)");
        CMDUtils.sendCMD(sender, "/mdis text seethrough", " (Toggle see through setting of your selected text display)");
        CMDUtils.sendCMD(sender, "/mdis text align <left | right | center>", " (Set your selected text display's text alignment)");
        CMDUtils.sendCMD(sender, "/mdis text linewidth <width>", " (Set the line width of your selected text display)");
        CMDUtils.sendCMD(sender, "/mdis text background <color | hex-code> <0-1>", " (Set the background color of a text display, and the opacity)");
        CMDUtils.sendCMD(sender, "/mdis text opacity <0-1>", " (Set the text opacity for your selected text display)");
    }
}
