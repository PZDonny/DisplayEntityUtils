package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.Direction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class DisplayEntityPluginCommand implements CommandExecutor {

    private static final HashMap<String, SubCommand> subCommands = new HashMap<>();

    public DisplayEntityPluginCommand(){
        subCommands.put("listgroups", new ListGroupsCMD());
        subCommands.put("listanims", new ListAnimationsCMD());

        subCommands.put("group", new GroupCMD());
        subCommands.put("parts", new PartsCMD());
        subCommands.put("text", new TextCMD());
        subCommands.put("interaction", new InteractionCMD());
        subCommands.put("anim", new AnimCMD());
        subCommands.put("convert", new ConvertCMD());

    }

    public static List<String> getTabComplete(){
        return subCommands.keySet().stream().toList();
    }

    public static List<String> getInteractionTabComplete(){
        return InteractionCMD.getTabComplete();
    }

    public static List<String> getAnimationTabComplete(){
        return AnimCMD.getTabComplete();
    }

    public static List<String> getGroupTabComplete(){
        return GroupCMD.getTabComplete();
    }

    public static List<String> getPartsTabComplete(){
        return PartsCMD.getTabComplete();
    }

    public static List<String> getConvertTabComplete(){
        return ConvertCMD.getTabComplete();
    }

    public static List<String> getTextTabComplete(){
        return TextCMD.getTabComplete();
    }

    static void invalidDirection(CommandSender sender){
        sender.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Invalid direction type!");
        for (Direction d : Direction.values()){
            sender.sendMessage(Component.text("- ").append(Component.text(d.name().toLowerCase(), NamedTextColor.YELLOW)));
        }
    }

    static void noGroupSelection(Player player){
        player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"You have not selected a spawned display entity group!");
        player.sendMessage(Component.text("/mdis group selectnearest <interaction-distance>", NamedTextColor.GRAY));
    }

    static void noPartSelection(Player player){
        player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"You have not selected a part!");
        player.sendMessage(ChatColor.GRAY+"/mdis parts cycle <first | prev | next>");
        player.sendMessage(ChatColor.GRAY+"/mdis parts select <part-tag>");
    }

    static void noPartSelectionInteraction(Player player){
        player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"You must look at the interaction you wish to add the command to, or select a part!");
        player.sendMessage(ChatColor.GRAY+"/mdis parts cycle <first | prev | next>");
        player.sendMessage(ChatColor.GRAY+"/mdis parts select <part-tag>");
    }

    static boolean hasPermission(Player player, Permission permission){
        if (!player.hasPermission(permission.getPermission())){
            player.sendMessage(Component.text("You do not have permission to do that!", NamedTextColor.RED));
            return false;
        }
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(ChatColor.RED + "You cannot use this command in the console!");
            return true;
        }

        if (args.length == 0) {
            mainCommandHelp(sender);
            return true;
        }
        String arg = args[0];
        if (arg.equals("reload")){
            if (!hasPermission(p, Permission.RELOAD)) {
                return true;
            }
            DisplayEntityPlugin.getInstance().reloadPlugin(false);
            sender.sendMessage(DisplayEntityPlugin.pluginPrefix + ChatColor.YELLOW + "Plugin Reloaded!");
        }
        else if (arg.equals("help")){
            if (hasPermission(p, Permission.HELP)){
                mainCommandHelp(sender);
            }
        }
        else{
            SubCommand subCommand = subCommands.get(arg);
            if (subCommand == null){
                mainCommandHelp(sender);
            }
            else{
                subCommand.execute(p, args);
            }
        }
        return true;
    }


    static void mainCommandHelp(CommandSender sender){
        sender.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
        sender.sendMessage(Component.text("Valid storage is \"local\", \"mongodb\", \"mysql\", and \"all\"", NamedTextColor.DARK_AQUA));
        sendCMD(sender, "/mdis group");
        sendCMD(sender, "/mdis parts");
        sendCMD(sender, "/mdis text");
        sendCMD(sender, "/mdis interaction");
        sendCMD(sender, "/mdis anim");
        sendCMD(sender, "/mdis listgroups <storage> [page-number]");
        sendCMD(sender, "/mdis listanims <storage> [page-number]");
        sendCMD(sender, "/mdis reload");
    }




    static void sendCMD(CommandSender sender, String command){
        sendCMD(sender, command, "");
    }

    static void sendCMD(CommandSender sender, String command, String description){
        sender.sendMessage(Component.text(command, NamedTextColor.GRAY)
                .append(Component.text(description, NamedTextColor.YELLOW)));
    }

}
