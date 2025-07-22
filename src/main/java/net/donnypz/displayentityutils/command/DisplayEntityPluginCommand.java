package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
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
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
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
        subCommands.put("listgroups", new ListGroupsCMD());
        subCommands.put("listanims", new ListAnimationsCMD());

        subCommands.put("group", new GroupCMD());
        subCommands.put("parts", new PartsCMD());
        subCommands.put("item", new ItemCMD());
        subCommands.put("text", new TextCMD());
        subCommands.put("interaction", new InteractionCMD());
        subCommands.put("anim", new AnimCMD());
        subCommands.put("bdengine", new BDEngineCMD());
        subCommands.put("reload", new ReloadCMD());

    }

    public List<String> getTabComplete(){
        return subCommands.keySet().stream().toList();
    }

    private List<String> getTabCompleteCommands(String subcommand, String current){
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
        sender.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Invalid direction type!", NamedTextColor.RED)));
        for (Direction d : Direction.values()){
            sender.sendMessage(Component.text("- ").append(Component.text(d.name().toLowerCase(), NamedTextColor.YELLOW)));
        }
    }

    public static void noGroupSelection(Player player){
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("You have not selected a spawned display entity group!", NamedTextColor.RED)));
        player.sendMessage(Component.text("/mdis group selectnearest <interaction-distance>", NamedTextColor.GRAY));
    }

    public static void noPartSelection(Player player){
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("You have not selected a part!", NamedTextColor.RED)));
        player.sendMessage(Component.text("/mdis parts cycle <first | prev | next>", NamedTextColor.GRAY));
    }

    public static void invalidTag(Player player, String tag){
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Failed to add tag: "+tag, NamedTextColor.RED)));
        invalidTagRestrictions(player);
    }

    public static void invalidTagRestrictions(Player player){
        player.sendMessage(Component.text("| Valid tags do not start with an \"!\" and do not contain commas.", NamedTextColor.GRAY, TextDecoration.ITALIC));
        player.sendMessage(Component.text("| The tag may also already exist or be set", NamedTextColor.GRAY, TextDecoration.ITALIC));
    }

    public static void suggestUpdateSelection(Player player){
        player.sendMessage(Component.text("| It is recommended to update/reset your part selection after adding parts!", NamedTextColor.GRAY));
        player.sendMessage(Component.text("| Quickly reset with \"/mdis parts refresh", NamedTextColor.GRAY));
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
        sender.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
        sender.sendMessage(Component.text("v"+DisplayEntityPlugin.getInstance().getPluginMeta().getVersion(), NamedTextColor.GRAY));
        CMDUtils.sendCMD(sender, "/mdis group", "Display Entity Models/Groups related commands");
        CMDUtils.sendCMD(sender, "/mdis anim", "Animation related commands");
        CMDUtils.sendCMD(sender, "/mdis parts", "Commands related to the parts (individual display entities) of a Display Entity Model/Group");
        CMDUtils.sendCMD(sender, "/mdis item", "Commands related to the Item Display parts of a Display Entity Model/Group");
        CMDUtils.sendCMD(sender, "/mdis text", "Commands related to the Text Display parts of a Display Entity Model/Group");
        CMDUtils.sendCMD(sender, "/mdis interaction", "Commands related to manipulating Interaction entities");
        CMDUtils.sendCMD(sender, "/mdis listgroups <storage> [page-number]", "List all saved Display Entity Models/Groups");
        CMDUtils.sendCMD(sender, "/mdis listanims <storage> [page-number]", "List all saved Animations");
        CMDUtils.sendCMD(sender, "/mdis bdengine", "Import/Convert models from BDEngine");
        CMDUtils.sendCMD(sender, "/mdis reload <config | controllers>", "Reload the plugin's config or Display Controllers." +
                " To reload Local, MySQL or MongoDB config save options, the server must be restarted");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.HELP.getPermission())){
            return empty;
        }
        if (args.length == 1) {
            return getTabComplete();
        }

        List<String> suggestions = new ArrayList<>();
        if (args.length == 2){
            String subcmd = args[0].toLowerCase();
            String current = args[1];
            switch (subcmd) {
                case "interaction", "anim", "group", "parts", "bdengine", "text", "item" -> {
                    return getTabCompleteCommands(subcmd, current);
                }
                case "listgroups", "listanims" -> addStorages(suggestions);
                case "reload" -> {
                    suggestions.add("config");
                    suggestions.add("controllers");
                }
            }
        }
        else if (args.length == 3){
            switch (args[0].toLowerCase()){
                case "group" -> {
                    switch (args[1].toLowerCase()){
                        case "selectnearest" -> {
                            suggestions.add("<interaction-distance>");
                        }
                        case "glowcolor" -> {
                            addColors(suggestions);
                        }
                        case "move", "translate" -> {
                            addDirections(suggestions);
                        }
                        case "save" -> {
                            addStorages(suggestions);
                        }
                        case "billboard" -> {
                            addBillboard(suggestions);
                        }
                        case "ride", "dismount" -> {
                            return null;
                        }
                    }
                }
                case "anim" -> {
                    if (args[1].equalsIgnoreCase("save")) {
                        addStorages(suggestions);
                    }
                }
                case "parts" -> {
                    switch (args[1].toLowerCase()){
                        case "glowcolor" -> {
                            addColors(suggestions);
                        }
                        case "move", "translate" -> {
                            addDirections(suggestions);
                        }
                        case "filtertypes", "create" -> {
                            suggestions.add("block");
                            suggestions.add("item");
                            suggestions.add("text");
                            suggestions.add("interaction");
                        }
                        case "cycle" -> {
                            suggestions.add("first");
                            suggestions.add("prev");
                            suggestions.add("next");
                            suggestions.add("last");
                        }
                        case "seeduuids" -> {
                            suggestions.add("group");
                            suggestions.add("selection");
                        }
                        case "setblock" -> {
                            suggestions.add("-target");
                            suggestions.add("-held");
                            suggestions.add("block-id");
                        }
                        case "info" -> {
                            suggestions.add("part");
                            suggestions.add("selection");
                        }
                        case "billboard" -> {
                            addBillboard(suggestions);
                        }
                    }
                }
                case "item" -> {
                    switch (args[1].toLowerCase()){
                        case "set" -> {
                            suggestions.add("-held");
                            suggestions.add("item-id");
                        }
                        case "transform" -> {
                            for (ItemDisplay.ItemDisplayTransform transform : ItemDisplay.ItemDisplayTransform.values()){
                                suggestions.add(transform.name());
                            }
                        }
                    }
                }
                case "interaction" -> {
                    if (args[1].equalsIgnoreCase("addcmd")){
                        suggestions.add("player");
                        suggestions.add("console");
                    }
                }
                case "text" -> {
                    if (args[1].equalsIgnoreCase("background")){
                        addColors(suggestions);
                    }
                    else if (args[1].equalsIgnoreCase("font")){
                        suggestions.add("default");
                        suggestions.add("uniform");
                        suggestions.add("alt");
                        suggestions.add("illageralt");
                    }
                }
            }
        }
        else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("group")){
                if (args[1].equalsIgnoreCase("spawn") ||args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("setspawnanim")){
                    addStorages(suggestions);
                }
                else if (args[1].equalsIgnoreCase("dismount")){
                    return null;
                }
            }
            else if (args[0].equalsIgnoreCase("anim")){
                if (args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("select")){
                    addStorages(suggestions);
                }
            }

            else if (args[0].equalsIgnoreCase("interaction")){
                if (args[1].equalsIgnoreCase("addcmd")){
                    suggestions.add("left");
                    suggestions.add("right");
                    suggestions.add("both");
                }
            }
        }
        else if (args.length == 5){
            if (args[0].equalsIgnoreCase("group")){
                if (args[1].equalsIgnoreCase("setspawnanim")){
                    suggestions.add("linear");
                    suggestions.add("loop");
                }
            }
        }
        return suggestions;
    }

    private void addDirections(List<String> suggestions){
        for (Direction dir : Direction.values()){
            suggestions.add(dir.name().toLowerCase());
        }
    }

    private void addBillboard(List<String> suggestions){
        for (Display.Billboard billboard : Display.Billboard.values()){
            suggestions.add(billboard.name());
        }
    }

    private void addStorages(List<String> suggestions){
        suggestions.add("all");
        suggestions.add("local");
        suggestions.add("mysql");
        suggestions.add("mongodb");
    }

    private void addColors(List<String> suggestions){
        suggestions.add("<hex-color>");
        suggestions.add("white");
        suggestions.add("silver");
        suggestions.add("gray");
        suggestions.add("black");
        suggestions.add("red");
        suggestions.add("maroon");
        suggestions.add("yellow");
        suggestions.add("olive");
        suggestions.add("lime");
        suggestions.add("green");
        suggestions.add("aqua");
        suggestions.add("teal");
        suggestions.add("blue");
        suggestions.add("navy");
        suggestions.add("fuchsia");
        suggestions.add("purple");
        suggestions.add("orange");
    }
}
