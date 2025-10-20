package net.donnypz.displayentityutils.command.text;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

public final class TextCMD extends ConsoleUsableSubCommand {

    public TextCMD(){
        super(Permission.HELP, new TextHelpCMD());
        new TextEditCMD(this);
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

    static void help(CommandSender sender, int page){
        sender.sendMessage(DisplayAPI.pluginPrefixLong);
        CMDUtils.sendCMD(sender, "/mdis text help", "Get help for text displays");
        CMDUtils.sendCMD(sender, "/mdis text edit [-&]", "Open a dialog menu to edit all text display properties. Add the \"-&\" parameter to format the text with \"&\"");
        CMDUtils.sendCMD(sender, "/mdis text set <text>", "Set this text for your selected text display");
        CMDUtils.sendCMD(sender, "/mdis text font <default | alt | uniform | illageralt> [-all]", "Set the text font for your selected text display");
        CMDUtils.sendCMD(sender, "/mdis text shadow [-all <on | off>]", "Toggle shadows visibility in your selected text display");
        CMDUtils.sendCMD(sender, "/mdis text seethrough [-all <on | off>]", "Toggle see through setting of your selected text display");
        CMDUtils.sendCMD(sender, "/mdis text align <left | right | center> [-all]", "Set your selected text display's text alignment");
        CMDUtils.sendCMD(sender, "/mdis text linewidth <width> [-all]", "Set the line width of your selected text display");
        CMDUtils.sendCMD(sender, "/mdis text background <color | hex-code> <0-1> [-all]", "Set the background color of a text display, and the opacity");
        CMDUtils.sendCMD(sender, "/mdis text opacity <0-1> [-all]", "Set the text opacity for your selected text display");
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray><bold>--------------------------"));
    }
}
