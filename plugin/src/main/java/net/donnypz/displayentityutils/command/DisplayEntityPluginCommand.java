package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.anim.AnimCMD;
import net.donnypz.displayentityutils.command.bdengine.BDEngineCMD;
import net.donnypz.displayentityutils.command.group.GroupCMD;
import net.donnypz.displayentityutils.command.interaction.InteractionCMD;
import net.donnypz.displayentityutils.command.item.ItemCMD;
import net.donnypz.displayentityutils.command.parts.PartsCMD;
import net.donnypz.displayentityutils.command.text.TextCMD;
import net.donnypz.displayentityutils.utils.Direction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ApiStatus.Internal
public class DisplayEntityPluginCommand implements TabExecutor {

    private final HashMap<String, DEUSubCommand> subCommands = new HashMap<>();
    private final List<String> empty = List.of();

    public DisplayEntityPluginCommand(){
        subCommands.put("listgroups", new ListCMD(
                Component.text("Incorrect Usage! /deu listgroups <storage> [page-number]", NamedTextColor.RED),
                2,
                true));
        subCommands.put("listanims", new ListCMD(
                Component.text("Incorrect Usage! /deu listanims <storage> [page-number]", NamedTextColor.RED),
                2,
                false));

        subCommands.put("hidepoints", new HidePointsCMD());
        subCommands.put("group", new GroupCMD());
        subCommands.put("parts", new PartsCMD());
        subCommands.put("item", new ItemCMD());
        subCommands.put("text", new TextCMD());
        subCommands.put("interaction", new InteractionCMD());
        subCommands.put("anim", new AnimCMD());
        subCommands.put("bdengine", new BDEngineCMD());
        subCommands.put("reload", new ReloadCMD());

    }

    public List<String> getFirstArgTabComplete(String current){
        List<String> list = new ArrayList<>();
        for (String s : subCommands.keySet()){
            if (s.startsWith(current.toLowerCase())){
                list.add(s);
            }
        }
        return list;
    }

    private List<String> getTabComplete(String commandType, String subCommand, String[] args){
        DEUSubCommand cmd = subCommands.get(commandType);
        if (cmd == null) return List.of();
        cmd = cmd.subCommands.get(subCommand);
        if (cmd == null) return List.of();

        String current = args[args.length-1];
        DEUSubCommand.TabSuggestion indexSuggestions = cmd.tabCompleteSuggestions.get(args.length-1);
        if (indexSuggestions == null) return List.of();

        List<String> tabCompletes = indexSuggestions.suggestions;
        if (tabCompletes == null) return List.of();

        if (!indexSuggestions.suggestUsingCurrentString){
            return tabCompletes;
        }
        List<String> list = new ArrayList<>();
        for (String s : tabCompletes){
            if (s.toLowerCase().startsWith(current.toLowerCase())){
                list.add(s);
            }
        }

        return list.isEmpty() ? tabCompletes : list;
    }

    private List<String> getTabComplete(String subcommand, String current){
        DEUSubCommand cmd = subCommands.get(subcommand);
        List<String> list = new ArrayList<>();
        for (String s : cmd.subCommands.keySet()){
            if (s.startsWith(current.toLowerCase())){
                list.add(s);
            }
        }
        return list;
    }


    public static void invalidDirection(CommandSender sender){
        sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid direction type!", NamedTextColor.RED)));
        for (Direction d : Direction.values()){
            sender.sendMessage(Component.text("- ").append(Component.text(d.name().toLowerCase(), NamedTextColor.YELLOW)));
        }
    }

    public static void noGroupSelection(Player player){
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You must have selected a display entity group to run this command!", NamedTextColor.RED)));
        player.sendMessage(Component.text("/deu group selectnearest <distance>", NamedTextColor.GRAY));
    }

    public static void disallowPacketGroup(Player player){
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You cannot do this with a packet-based group!", NamedTextColor.RED)));
    }

    public static void noPartSelection(Player player){
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You have not selected a part!", NamedTextColor.RED)));
        player.sendMessage(Component.text("/deu parts cycle <first | prev | next>", NamedTextColor.GRAY));
    }

    public static void invalidTag(Player player, String tag){
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Failed to add tag: "+tag, NamedTextColor.RED)));
        invalidTagRestrictions(player);
    }

    public static void invalidTagRestrictions(Player player){
        player.sendMessage(Component.text("| Valid tags do not start with an \"!\" and do not contain commas.", NamedTextColor.GRAY, TextDecoration.ITALIC));
        player.sendMessage(Component.text("| The tag may also already exist or be set", NamedTextColor.GRAY, TextDecoration.ITALIC));
    }

    public static void invalidStorage(CommandSender sender){
        sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid Storage!", NamedTextColor.RED)));
        sender.sendMessage(Component.text("<gray>| Valid Storages: local, mysql, mongodb>", NamedTextColor.GRAY));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            mainCommandHelp(sender);
            return true;
        }
        String arg = args[0];
        DEUSubCommand subCommand = subCommands.get(arg);
        if (subCommand == null){
            mainCommandHelp(sender);
        }
        else{
            executeCommand(subCommand, sender, args);
        }
        return true;
    }

    public static void executeCommand(DEUSubCommand subCommand, CommandSender sender, String[] args){
        if (!hasPermission(sender, subCommand.getPermission())){
            return;
        }
        if (subCommand instanceof ConsoleUsableSubCommand c){
            c.execute(sender, args);
        }
        else if (subCommand instanceof PlayerSubCommand c){
            if (!(sender instanceof Player p)) {
                sender.sendMessage(Component.text("You cannot use this command in the console!", NamedTextColor.RED));
                return;
            }
            c.execute(p, args);
        }
    }

    public static boolean hasPermission(@NotNull CommandSender sender, @NotNull Permission permission){
        if (!sender.hasPermission(permission.getPermission())){
            sender.sendMessage(Component.text("You do not have permission to do that!", NamedTextColor.RED));
            return false;
        }
        return true;
    }


    static void mainCommandHelp(CommandSender sender){
        if (!hasPermission(sender, Permission.HELP)) {
            return;
        }
        sender.sendMessage(DisplayAPI.pluginPrefixLong);
        sender.sendMessage(Component.text("v"+DisplayAPI.getVersion(), NamedTextColor.GRAY));
        CMDUtils.sendCMD(sender, "/deu group", "Display Entity Models/Groups related commands");
        CMDUtils.sendCMD(sender, "/deu anim", "Animation related commands");
        CMDUtils.sendCMD(sender, "/deu parts", "Commands related to the parts (individual display entities) of a Display Entity Model/Group");
        CMDUtils.sendCMD(sender, "/deu item", "Commands related to the Item Display parts of a Display Entity Model/Group");
        CMDUtils.sendCMD(sender, "/deu text", "Commands related to the Text Display parts of a Display Entity Model/Group");
        CMDUtils.sendCMD(sender, "/deu interaction", "Commands related to manipulating Interaction entities");
        CMDUtils.sendCMD(sender, "/deu listgroups <storage> [page-number]", "List all saved Display Entity Models/Groups");
        CMDUtils.sendCMD(sender, "/deu listanims <storage> [page-number]", "List all saved animations");
        CMDUtils.sendCMD(sender, "/deu hidepoints", "Hide any visible points (frame points, persistent packet group points, etc.)");
        CMDUtils.sendCMD(sender, "/deu bdengine", "Import/Convert models from BDEngine");
        CMDUtils.sendCMD(sender, "/deu reload <config | controllers>", "Reload the plugin's config or Display Controllers." +
                " To reload Local, MySQL or MongoDB config save options, the server must be restarted");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.HELP.getPermission())){
            return empty;
        }
        if (args.length == 1) {
            return getFirstArgTabComplete(args[0]);
        }

        List<String> suggestions = new ArrayList<>();
        if (args.length == 2){
            String subcmd = args[0].toLowerCase();
            String current = args[1];
            switch (subcmd) {
                case "interaction", "anim", "group", "parts", "bdengine", "text", "item" -> {
                    return getTabComplete(subcmd, current);
                }
                case "listgroups", "listanims" -> suggestions.addAll(DEUSubCommand.TabSuggestion.STORAGES.suggestions);
                case "reload" -> {
                    suggestions.add("config");
                    suggestions.add("controllers");
                }
            }
        }
        else{
            return getTabComplete(args[0], args[1], args);
        }
        return suggestions;
    }
}
