package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

class ItemCMD implements ConsoleUsableSubCommand{

    private static final HashMap<String, SubCommand> subCommands = new HashMap<>();


    ItemCMD(){
        subCommands.put("help", new ItemHelpCMD());
        subCommands.put("set", new ItemSetCMD());
        subCommands.put("toggleglint", new ItemToggleGlintCMD());
        subCommands.put("transform", new ItemTransformCMD());
    }

    static List<String> getTabComplete(){
        return subCommands.keySet().stream().toList();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2){
            itemHelp(sender);
            return;
        }
        String arg = args[1];
        SubCommand subCommand = subCommands.get(arg);
        if (subCommand == null){
            if (!DisplayEntityPluginCommand.hasPermission(sender, Permission.HELP)){
                return;
            }
            itemHelp(sender);
        }
        else{
            DisplayEntityPluginCommand.executeCommand(subCommand, sender, args);
        }
    }

    static void itemHelp(CommandSender sender){
        sender.sendMessage(Component.empty());
        sender.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
        sender.sendMessage(Component.text("| Commands with \"-all\" will apply the command to all item displays within a part selection", NamedTextColor.GOLD));
        CMDUtils.sendCMD(sender,"/mdis item help", " (Get help for item displays)");
        CMDUtils.sendCMD(sender, "/mdis item set <\"-held\" | item-id> [-all]", " (Change the item of a item display part)");
        CMDUtils.sendCMD(sender, "/mdis item transform <transform-type> [-all]", "(Change the item display transform of a item display part)");
        CMDUtils.sendCMD(sender,"/mdis item toggleglint [-all]", " (Toggle the enchantment glint of an item display's item)");
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray><bold>--------------------------"));
    }
}
