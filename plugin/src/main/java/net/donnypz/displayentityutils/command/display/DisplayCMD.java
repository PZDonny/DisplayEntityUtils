package net.donnypz.displayentityutils.command.display;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

public final class DisplayCMD extends ConsoleUsableSubCommand {


    public DisplayCMD(){
        super(Permission.HELP, new DisplayHelpCMD());
        new DisplayGlowColorCMD(this);
        new DisplayBrightnessCMD(this);
        new DisplayViewRangeCMD(this);
        new DisplayBillboardCMD(this);
        new DisplayTranslateCMD(this);
        new DisplayScaleCMD(this);
        new DisplaySetBlockCMD(this);
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
        sender.sendMessage(Component.empty());
        sender.sendMessage(DisplayAPI.pluginPrefixLong);
        sender.sendMessage(Component.text("| Commands with \"-all\" will apply the command to all display entities in your selection", NamedTextColor.GOLD));

            CMDUtils.sendCMD(sender, "/deu display glowcolor <color | hex-code> [-all]", "Set your selected display part's glow color");
            CMDUtils.sendCMD(sender, "/deu display brightness <block> <sky> [-all]", "Set your selected display part's brightness. Enter values between 0-15. -1 resets");
            CMDUtils.sendCMD(sender, "/deu display viewrange <view-range-multiplier> [-all]", "Set the view range multiplier for your selected display part");
            CMDUtils.sendCMD(sender, "/deu display billboard <fixed | vertical | horizontal | center> [-all]", "Set the billboard of your selected display part");
            CMDUtils.sendCMD(sender, "/deu display translate <direction> <distance> <tick-duration> [-all]", "Translate your selected display part");
            CMDUtils.sendCMD(sender, "/deu display scale <x | y | z | -all> <scale> [-all]", "Change the scale of your selected display part, by axis");
            CMDUtils.sendCMD(sender, "/deu display setblock <\"-held\" | \"-target\" | block-id> [-all]", "Change the block of a block display part");
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray><bold>--------------------------"));
        //sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray><bold>----------</bold><yellow>Page "+page+"<gray><bold>----------"));
    }
}
