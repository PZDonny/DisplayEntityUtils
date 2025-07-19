package net.donnypz.displayentityutils.command.text;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.*;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class TextCMD extends ConsoleUsableSubCommand {

    public TextCMD(){
        super(Permission.HELP, true);
        new TextMenuCMD(this);
        new TextSetCMD(this);
        new TextFontCMD(this);
        new TextShadowCMD(this);
        new TextSeeThroughCMD(this);
        new TextAlignCMD(this);
        new TextLineWidthCMD(this);
        new TextBackgroundCMD(this);
        new TextOpacityCMD(this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2){
            textHelp(sender);
            return;
        }
        String arg = args[1];
        DEUSubCommand subCommand = subCommands.get(arg);
        if (subCommand == null){
            textHelp(sender);
        }
        else{
            DisplayEntityPluginCommand.executeCommand(subCommand, sender, args);
        }
    }

    static void textHelp(CommandSender sender){
        sender.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
        CMDUtils.sendCMD(sender, "/mdis text help", "Get help for text displays");
        CMDUtils.sendCMD(sender, "/mdis text menu [-&]", "Open a dialog menu to edit all text display properties. Add the \"-&\" parameter to format the text with \"&\"");
        CMDUtils.sendCMD(sender, "/mdis text set <text>", "Set this text for your selected text display");
        CMDUtils.sendCMD(sender, "/mdis text font <default | alt | uniform | illageralt>", "Set the text font for your selected text display");
        CMDUtils.sendCMD(sender, "/mdis text shadow", "Toggle shadows visibility in your selected text display");
        CMDUtils.sendCMD(sender, "/mdis text seethrough", "Toggle see through setting of your selected text display");
        CMDUtils.sendCMD(sender, "/mdis text align <left | right | center>", "Set your selected text display's text alignment");
        CMDUtils.sendCMD(sender, "/mdis text linewidth <width>", "Set the line width of your selected text display");
        CMDUtils.sendCMD(sender, "/mdis text background <color | hex-code> <0-1>", "Set the background color of a text display, and the opacity");
        CMDUtils.sendCMD(sender, "/mdis text opacity <0-1>", "Set the text opacity for your selected text display");
    }
}
