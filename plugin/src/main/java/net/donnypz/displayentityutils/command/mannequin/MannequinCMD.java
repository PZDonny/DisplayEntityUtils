package net.donnypz.displayentityutils.command.mannequin;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

public class MannequinCMD extends ParentSubCommand {
    public MannequinCMD() {
        super("mannequin");
        new MannequinSpawnCMD(this);
        new MannequinNameCMD(this);
        new MannequinUnnameCMD(this);
        new MannequinBelowNameCMD(this);
        new MannequinToggleNameVisibilityCMD(this);
        new MannequinSkinCMD(this);
        new MannequinPoseCMD(this);
        new MannequinPivotCMD(this);
        new MannequinScaleCMD(this);
        new MannequinToggleGravityCMD(this);
        new MannequinToggleImmovableCMD(this);
        new MannequinMainHandCMD(this);
        new MannequinEquipmentCMD(this);
    }

    static void help(CommandSender sender, int page){
        sender.sendMessage(Component.empty());
        sender.sendMessage(DisplayAPI.pluginPrefixLong);
        if (page == 1){
            sender.sendMessage(Component.text("| Commands with \"-all\" will apply the command to all mannequins within a part selection", NamedTextColor.GOLD));
            CMDUtils.sendCMD(sender, "/deu mannequin help", "Get help for mannequins");
            CMDUtils.sendCMD(sender, "/deu mannequin spawn [-g]", "Spawn a mannequin at your location.\nUse \"-g\" to add it to your selected group");
            CMDUtils.sendCMD(sender, "/deu mannequin name <name>", "Set your selected mannequin's name");
            CMDUtils.sendCMD(sender, "/deu mannequin unname [-all]", "Remove your selected mannequin's name");
            CMDUtils.sendCMD(sender, "/deu mannequin belowname <text>", "Set the text below your selected mannequin's name");
            CMDUtils.sendCMD(sender, "/deu mannequin togglenamevisibility [-all <on | off>]", "Toggle the gravity of an mannequin");
            CMDUtils.sendCMD(sender, "/deu mannequin skin <player-name> [-all]", "Set your selected mannequin's skin");
        }
        else{
            CMDUtils.sendCMD(sender, "/deu mannequin togglegravity [-all <on | off>]", "Toggle the gravity of an mannequin");
            CMDUtils.sendCMD(sender, "/deu mannequin toggleimmovable [-all <on | off>]", "Toggle the immovability of an mannequin");
            CMDUtils.sendCMD(sender, "/deu mannequin equipment ", "Open a GUI to edit your selected mannequin's equipment.");
            CMDUtils.sendCMD(sender, "/deu mannequin mainhand <left | right>", "Set your selected mannequin's main hand");
            CMDUtils.sendCMD(sender, "/deu mannequin pivot <angle> [-all]");
            CMDUtils.sendCMD(sender, "/deu mannequin pose <pose> [-all]", "Change your selected mannequin's pose");
            CMDUtils.sendCMD(sender, "/deu mannequin scale <scale> [-all]", "Set your selected mannequin's scale");
        }
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray><bold>----------</bold><yellow>Page "+page+"<gray><bold>----------"));
    }
}
