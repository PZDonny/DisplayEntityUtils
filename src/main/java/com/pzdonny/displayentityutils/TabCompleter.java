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
        if (args.length == 1){
            suggestions.add("help");
            suggestions.add("partshelp");
            suggestions.add("selectnearest");
            suggestions.add("addinteractions");
            suggestions.add("removeinteractions");
            suggestions.add("settag");
            suggestions.add("gettag");
            suggestions.add("highlight");
            suggestions.add("highlightparts");
            suggestions.add("clone");
            suggestions.add("movehere");
            suggestions.add("move");
            suggestions.add("translate");
            suggestions.add("despawn");
            suggestions.add("save");
            suggestions.add("delete");
            suggestions.add("spawn");
            suggestions.add("list");
            suggestions.add("reload");
            suggestions.add("cycleparts");
            suggestions.add("setparttag");
            suggestions.add("getparttag");
            suggestions.add("selectparts");
            return suggestions;
        }
        else if (args.length == 2){
            if (args[0].equalsIgnoreCase("save") || args[0].equalsIgnoreCase("list")){
                if (args[0].equals("save")){
                    suggestions.add("all");
                }
                for (DisplayGroupManager.LoadMethod method : DisplayGroupManager.LoadMethod.values()){
                    suggestions.add(method.name().toLowerCase());
                }
                return suggestions;
            }
            else if (args[0].equalsIgnoreCase("move") || args[0].equalsIgnoreCase("translate") || args[0].equalsIgnoreCase("translateparts")){
                for (Direction direction : Direction.values()){
                    suggestions.add(direction.name().toLowerCase());
                }
                return suggestions;
            }
            else if (args[0].equals("cyclepart")){
                suggestions.add("first");
                suggestions.add("prev");
                suggestions.add("next");
                return suggestions;
            }
        }
        else if (args.length == 3){
            if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("spawn")){
                suggestions.add("all");
                for (DisplayGroupManager.LoadMethod method : DisplayGroupManager.LoadMethod.values()){
                    suggestions.add(method.name().toLowerCase());
                }
                return suggestions;
            }
        }
        return null;
    }
}
