package net.donnypz.displayentityutils.command.item;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

public final class ItemCMD extends ParentSubCommand {


    public ItemCMD(){
        super("item");
        new ItemSetCMD(this);
        new ItemToggleGlintCMD(this);
        new ItemTransformCMD(this);
    }

    static void help(CommandSender sender, int page){
        sender.sendMessage(Component.empty());
        sender.sendMessage(DisplayAPI.pluginPrefixLong);
        sender.sendMessage(Component.text("| Commands with \"-all\" will apply the command to all item displays within a part selection", NamedTextColor.GOLD));
        CMDUtils.sendCMD(sender,"/deu item help", "Get help for item displays");
        CMDUtils.sendCMD(sender, "/deu item set <\"-held\" | item-id> [-all]", "Change the item of a item display part");
        CMDUtils.sendCMD(sender, "/deu item transform <transform-type> [-all]", "Change the item display transform of a item display part");
        CMDUtils.sendCMD(sender,"/deu item toggleglint [-all <on | off>]", "Toggle the enchantment glint of an item display's item");
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray><bold>--------------------------"));
    }
}
