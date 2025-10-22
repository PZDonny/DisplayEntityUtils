package net.donnypz.displayentityutils.command.item;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

public final class ItemCMD extends ConsoleUsableSubCommand {


    public ItemCMD(){
        super(Permission.HELP, new ItemHelpCMD());
        new ItemSetCMD(this);
        new ItemToggleGlintCMD(this);
        new ItemTransformCMD(this);
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
        sender.sendMessage(Component.text("| Commands with \"-all\" will apply the command to all item displays within a part selection", NamedTextColor.GOLD));
        CMDUtils.sendCMD(sender,"/mdis item help", "Get help for item displays");
        CMDUtils.sendCMD(sender, "/mdis item set <\"-held\" | item-id> [-all]", "Change the item of a item display part");
        CMDUtils.sendCMD(sender, "/mdis item transform <transform-type> [-all]", "Change the item display transform of a item display part");
        CMDUtils.sendCMD(sender,"/mdis item toggleglint [-all <on | off>]", "Toggle the enchantment glint of an item display's item");
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray><bold>--------------------------"));
    }
}
