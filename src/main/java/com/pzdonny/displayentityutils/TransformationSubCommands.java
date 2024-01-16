package com.pzdonny.displayentityutils;

import com.pzdonny.displayentityutils.managers.DisplayGroupManager;
import com.pzdonny.displayentityutils.utils.Direction;
import com.pzdonny.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import com.pzdonny.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class TransformationSubCommands {

    static void setYaw(Player p, String yawString){
        try{
            SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(p);
            if (group == null){
                MainCommand.noSelection(p);
                return;
            }
            double yaw = Double.parseDouble(yawString);
            group.setYaw((float) yaw);
            p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Yaw set!");
        }
        catch(NumberFormatException e){
            p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Please enter a valid number!");
        }
    }

    static void move(Player p, String[] args, SpawnedDisplayEntityGroup group){
        try{
            Direction direction = Direction.valueOf(args[2].toUpperCase());
            double distance = Double.parseDouble(args[3]);
            if (distance <= 0){
                p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Enter a number greater than 0 for the distance!");
                return;
            }
            int duration = Integer.parseInt(args[4]);
            if (duration <= 0){
                p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Enter a whole number greater than 0 for the duration!");
                return;
            }
            p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Moving spawned display entity group!");
            group.teleportMove(direction, distance, duration);
        }
        catch(IllegalArgumentException e){
            if (e instanceof NumberFormatException){
                p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Please enter valid numbers!");
            }
            else{
                MainCommand.invalidDirection(p);
            }
        }
    }


    static void translate(Player p, String[] args, SpawnedDisplayEntityGroup group){
        try{
            Direction direction = Direction.valueOf(args[2].toUpperCase());
            double distance = Double.parseDouble(args[2]);
            if (distance <= 0){
                p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Enter a number greater than 0 for the distance!");
                return;
            }
            int duration = Integer.parseInt(args[3]);
            if (duration <= 0){
                duration = 0;
            }
            group.translate(direction, (float) distance, duration);
            p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Translating spawned display entity group!");
        }
        catch(IllegalArgumentException e){
            if (e instanceof NumberFormatException){
                p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Enter valid integer numbers!");
            }
            else{
                MainCommand.invalidDirection(p);
            }
        }
    }

    static void translateParts(Player p, String[] args, SpawnedPartSelection partSelection){
        try{
            Direction direction = Direction.valueOf(args[2].toUpperCase());
            double distance = Double.parseDouble(args[3]);
            if (distance <= 0){
                p.sendMessage(DisplayEntityPlugin.pluginPrefix+ ChatColor.RED+"Enter a number greater than 0 for the distance!");
                return;
            }
            int duration = Integer.parseInt(args[4]);
            if (duration <= 0){
                duration = 0;
            }
            partSelection.translate((float) distance, duration, direction);
            p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Translating selected parts!");
        }
        catch(IllegalArgumentException e){
            if (e instanceof NumberFormatException){
                p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Enter valid integer numbers!");
            }
            else{
                MainCommand.invalidDirection(p);
            }
        }
    }

    static void moveHere(Player p){
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(p);
        if (group == null){
            MainCommand.noSelection(p);
            return;
        }
        boolean result = group.teleport(p.getLocation(), true);
        if (!result){
            p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Failed to move spawned display entity group to your location");
            return;
        }
        p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Moved spawned group to your location!");
    }
}
