package net.donnypz.displayentityutils.command.mannequin;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

public class MannequinCMD extends ConsoleUsableSubCommand {
    public MannequinCMD() {
        super(Permission.HELP, new MannequinHelpCMD());
        new MannequinNameCMD(this);
        new MannequinBelowNameCMD(this);
        new MannequinToggleNameVisibilityCMD(this);
        new MannequinSkinCMD(this);
        new MannequinPoseCMD(this);
        new MannequinScaleCMD(this);
        new MannequinToggleGravityCMD(this);
        new MannequinToggleImmovableCMD(this);
        new MannequinMainHandCMD(this);
        new MannequinHeldItemCMD(this);
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
        if (page == 1){
            sender.sendMessage(Component.text("| Commands with \"-all\" will apply the command to all mannequins within a part selection", NamedTextColor.GOLD));
            CMDUtils.sendCMD(sender, "/deu mannequin help", "Get help for mannequins");
            CMDUtils.sendCMD(sender, "/deu mannequin name <name>", "Set your selected mannequin's name");
            CMDUtils.sendCMD(sender, "/deu mannequin belowname <text>", "Set the text below your selected mannequin's name");
            CMDUtils.sendCMD(sender, "/deu mannequin togglenamevisibility [-all <on | off>]", "Toggle the gravity of an mannequin");
            CMDUtils.sendCMD(sender, "/deu mannequin skin <player-name> [-all]", "Set your selected mannequin's skin");
            CMDUtils.sendCMD(sender, "/deu mannequin togglegravity [-all <on | off>]", "Toggle the gravity of an mannequin");
            CMDUtils.sendCMD(sender, "/deu mannequin toggleimmovable [-all <on | off>]", "Toggle the immovability of an mannequin");
        }
        else{
            CMDUtils.sendCMD(sender, "/deu mannequin mainhand <left | right>", "Set the mannequin's main hand");
            CMDUtils.sendCMD(sender, "/deu mannequin helditem <main | off> <\"-held\" | item-id>", "Set the item in a mannequin's main or offhand");
            CMDUtils.sendCMD(sender, "/deu mannequin armor [-stop]", "Set the armor of a mannequin by clicking it with armor pieces. \"-stop\" stops editing");
            CMDUtils.sendCMD(sender, "/deu mannequin pose <pose> [-all]", "Change your selected mannequin's pose");
            CMDUtils.sendCMD(sender, "/deu mannequin scale <scale> [-all]", "Set your selected mannequin's scale");
            CMDUtils.sendCMD(sender, "/deu mannequin clone", "Clone a mannequin");
            CMDUtils.sendCMD(sender, "/deu mannequin clonehere", "Clone a mannequin at your current location");
        }
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray><bold>----------</bold><yellow>Page "+page+"<gray><bold>----------"));
    }
}
