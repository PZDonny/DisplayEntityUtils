package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

class ConvertCMD implements SubCommand{

    private static final HashMap<String, SubCommand> subCommands = new HashMap<>();


    ConvertCMD(){
        subCommands.put("datapack", new ConvertDatapackCMD());
        //subCommands.put("bdengine", new ConvertBDEngineCMD());
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
            conversionHelp(player);
        }
        else{
            subCommand.execute(player, args);
        }
    }

    static void conversionHelp(CommandSender sender){
        sender.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
        sender.sendMessage(Component.text("Convert files exported from BDEngine's website", NamedTextColor.AQUA));
        sender.sendMessage(Component.text("Animations datapacks and \".bdengine\" model files can be converted", NamedTextColor.AQUA));
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<aqua>Use <yellow>\"block-display.com\" <aqua>to create convertable models and animations"));
        DisplayEntityPluginCommand.sendCMD(sender,"/mdis convert datapack <datapack-name> <group-tag-to-set> <anim-tag-to-set>", "(Convert a animation datapack form \"block-display.com\" into a editable animation file)");
        DisplayEntityPluginCommand.sendCMD(sender, "/mdis convert bdengine <frame-id>");


    }


}
