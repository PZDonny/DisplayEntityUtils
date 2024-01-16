package com.pzdonny.displayentityutils;

import com.pzdonny.displayentityutils.managers.DisplayGroupManager;
import com.pzdonny.displayentityutils.utils.Direction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

class TabCompleter implements org.bukkit.command.TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            suggestions.add("help");
            suggestions.add("parts");
            suggestions.add("group");
            suggestions.add("list");
            suggestions.add("reload");
        }
        else if (args.length == 2){
            if (args[0].equalsIgnoreCase("parts")){
                suggestions.add("cycle");
                suggestions.add("settag");
                suggestions.add("gettag");
                suggestions.add("select");
                suggestions.add("remove");
                suggestions.add("glow");
                suggestions.add("translate");
                suggestions.add("setinteractioncommand");
                suggestions.add("removeinteractioncommand");
            }
            else if (args[0].equalsIgnoreCase("group")){
                suggestions.add("selectnearest");
                suggestions.add("spawn");
                suggestions.add("despawn");
                suggestions.add("save");
                suggestions.add("delete");
                suggestions.add("addinteractions");
                suggestions.add("removeinteractions");
                suggestions.add("settag");
                suggestions.add("gettag");
                suggestions.add("setyaw");
                suggestions.add("clone");
                suggestions.add("move");
                suggestions.add("translate");
                suggestions.add("movehere");
                suggestions.add("glow");
            }
        }
        else if (args.length == 3){
            if (args[1].equalsIgnoreCase("move") || args[1].equalsIgnoreCase("translate")){
                addDirections(suggestions);
            }
            if (args[1].equalsIgnoreCase("cycle")){
                suggestions.add("first");
                suggestions.add("prev");
                suggestions.add("next");
            }
        }
        else if (args.length == 4){
            if (args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("spawn")){
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

}
