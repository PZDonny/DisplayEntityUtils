package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.utils.Direction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public final class DisplayEntityPluginTabCompleter implements TabCompleter {

    public DisplayEntityPluginTabCompleter(){}

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 1) {
            return DisplayEntityPluginCommand.getTabComplete();
        }

        List<String> suggestions = new ArrayList<>();
        if (args.length == 2){
            String arg1 = args[0].toLowerCase();
            switch (arg1) {
                case "interaction" -> {
                    return DisplayEntityPluginCommand.getInteractionTabComplete();
                }
                case "anim" -> {
                    return DisplayEntityPluginCommand.getAnimationTabComplete();
                }
                case "group" -> {
                    return DisplayEntityPluginCommand.getGroupTabComplete();
                }
                case "parts" -> {
                    return DisplayEntityPluginCommand.getPartsTabComplete();
                }
                case "convert" -> {
                    return DisplayEntityPluginCommand.getConvertTabComplete();
                }
                case "text" -> {
                    return DisplayEntityPluginCommand.getTextTabComplete();
                }
                case "listgroups", "listanim" -> addStorages(suggestions);
            }
        }
        else if (args.length == 3){
            if (args[1].equalsIgnoreCase("setglowcolor") && (args[0].equalsIgnoreCase("group") || args[0].equalsIgnoreCase("parts"))){
                addColors(suggestions);
            }
            else if (args[1].equalsIgnoreCase("move") || args[1].equalsIgnoreCase("translate")){
                addDirections(suggestions);
            }
            else if (args[1].equalsIgnoreCase("cycle")){
                suggestions.add("first");
                suggestions.add("prev");
                suggestions.add("next");
            }
            else if (args[1].equals("selectnearest")){
                suggestions.add("<interaction-distance>");
            }
            else if (args[1].equalsIgnoreCase("seeduuids")){
                suggestions.add("group");
                suggestions.add("selection");
            }
            else if (args[1].equalsIgnoreCase("setblock")){
                suggestions.add("-target");
                suggestions.add("-held");
                suggestions.add("block-id");
            }
        }
        else if (args.length == 4) {
            if (args[1].equalsIgnoreCase("delete")
                    || args[1].equalsIgnoreCase("spawn")
                    || (args[1].equals("select") && args[0].equalsIgnoreCase("anim"))
                    || (args[1].equalsIgnoreCase("save") && (args[0].equalsIgnoreCase("group") || args[0].equalsIgnoreCase("anim")))){
                addStorages(suggestions);
            }
        }
        return suggestions;
    }

    private void addDirections(List<String> suggestions){
        for (Direction dir : Direction.values()){
            suggestions.add(dir.name().toLowerCase());
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
