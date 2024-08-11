package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

class TextCMD implements SubCommand{

    private static final HashMap<String, SubCommand> subCommands = new HashMap<>();


    TextCMD(){
        subCommands.put("set", new TextSetCMD());
        subCommands.put("font", new TextFontCMD());
        subCommands.put("shadow", new TextShadowCMD());
        subCommands.put("seethrough", new TextSeeThroughCMD());
        subCommands.put("align", new TextAlignCMD());
        subCommands.put("linewidth", new TextLineWidthCMD());
    }

    static List<String> getTabComplete(){
        return subCommands.keySet().stream().toList();
    }

    @Override
    public void execute(Player player, String[] args) {
        String arg = args[1];
        SubCommand subCommand = subCommands.get(arg);
        if (subCommand == null){
            if (!DisplayEntityPluginCommand.hasPermission(player, Permission.HELP)){
                return;
            }
            textHelp(player);
        }
        else{
            subCommand.execute(player, args);
        }
    }

    static void textHelp(CommandSender sender){
        sender.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
        DisplayEntityPluginCommand.sendCMD(sender,"/mdis text set <text>");
        DisplayEntityPluginCommand.sendCMD(sender,"/mdis text font <default | alt | uniform | illageralt>");
        DisplayEntityPluginCommand.sendCMD(sender,"/mdis text shadow", " (Toggle shadows visibility in text display)");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis text seethrough", "(Toggle see through setting of text display)");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis text align <left | right | center>", "(Set the text display's text alignment)");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis text linewidth <width>", " (Set the line width of text display)");
    }

}
