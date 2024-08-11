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

class PartsCMD implements SubCommand{

    private static final HashMap<String, SubCommand> subCommands = new HashMap<>();


    PartsCMD(){
        subCommands.put("cycle", new PartsCycleCMD());
        subCommands.put("glow", new PartsGlowCMD());
        subCommands.put("setglowcolor", new PartsSetGlowColorCMD());
        subCommands.put("select", new PartsSelectCMD());
        subCommands.put("addtag", new PartsAddTagCMD());
        subCommands.put("removetag", new PartsRemoveTagCMD());
        subCommands.put("listtags", new PartsListTagsCMD());
        subCommands.put("remove", new PartsRemoveCMD());
        subCommands.put("translate", new PartsTranslateCMD());
        subCommands.put("seeduuids", new PartsSeedUUIDsCMD());
        subCommands.put("setblock", new PartsSetBlockCMD());
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
            partsHelp(player);
        }
        else{
            subCommand.execute(player, args);
        }
    }

    static void partsHelp(CommandSender sender){
        sender.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
        sender.sendMessage(ChatColor.AQUA+"\"Parts\" are each individual display/interaction entity that is spawned within the group");
        sender.sendMessage(ChatColor.AQUA+"Add part tags to parts to identify each part in a group");
        sender.sendMessage(ChatColor.YELLOW+" | Mainly useful for API users, creating animations, and usage with addon plugins");
        sender.sendMessage(ChatColor.GRAY+"/mdis parts cycle <first | prev | next>");
        sender.sendMessage(ChatColor.GRAY+"/mdis parts addtag <part-tag>");
        sender.sendMessage(ChatColor.GRAY+"/mdis parts removetag <part-tag>");
        sender.sendMessage(ChatColor.GRAY+"/mdis parts listtags");
        sender.sendMessage(ChatColor.GRAY+"/mdis parts select <part-tag>");
        sender.sendMessage(ChatColor.GRAY+"/mdis parts remove");
        sender.sendMessage(ChatColor.GRAY+"/mdis parts glow");
        sender.sendMessage(ChatColor.GRAY+"/mdis parts setglowcolor <color | hex-code>");
        sender.sendMessage(ChatColor.GRAY+"/mdis parts translate <direction> <distance> <tick-duration>");
        sender.sendMessage(ChatColor.GRAY+"/mdis parts seeduuids <group | selection> <seed>"+ChatColor.YELLOW+" (Useful when wanting to use the same animation on similar groups)");
        sender.sendMessage(ChatColor.GRAY+"/mdis parts setblock <\"-held\" | \"-target\" | block-id>");
    }

    static void noPartSelection(Player player){
        player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"You have not selected a part!");
        player.sendMessage(ChatColor.GRAY+"/mdis parts cycle <first | prev | next>");
        player.sendMessage(ChatColor.GRAY+"/mdis parts select <part-tag>");
    }


}
