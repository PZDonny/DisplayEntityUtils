package com.pzdonny.displayentityutils;

import com.pzdonny.displayentityutils.managers.DisplayGroupManager;
import com.pzdonny.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import com.pzdonny.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DataSubCommands {


    static void spawn(Player p, String tag, String storage){
        if (storage.equals("all")){
            p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.YELLOW+"Attempting to spawn display from all storage locations");
            attemptAll(p, tag, DisplayGroupManager.LoadMethod.LOCAL);
            return;
        }
        DisplayGroupManager.LoadMethod loadMethod = DisplayGroupManager.LoadMethod.valueOf(storage.toUpperCase());
        if (!loadMethod.isEnabled()){
            p.sendMessage(ChatColor.GRAY+"- Storage location is disabled and cannot be checked!");
            return;
        }
        DisplayEntityGroup group = DisplayGroupManager.retrieveDisplayEntityGroup(loadMethod, tag);
        if (group == null){
            p.sendMessage(ChatColor.RED+"- Failed to find saved display entity group in that storage location!");
            return;
        }
        group.spawn(p.getLocation());
        p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Successfully spawned display entity group at your location! "+ChatColor.WHITE+"(Tagged: "+tag+")");
    }


    private static void attemptAll(Player p, String tag, DisplayGroupManager.LoadMethod storage){
        switch(storage){
            case LOCAL -> {
                p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.YELLOW+"Attempting to spawn display entity group "+ChatColor.WHITE+"(Tagged: "+tag+")");
                if (!DisplayEntityPlugin.isLocalEnabled()){
                    p.sendMessage(ChatColor.GRAY+"- Local storage is disabled, checking MongoDB...");
                    attemptAll(p, tag, DisplayGroupManager.LoadMethod.MONGODB);
                    return;
                }
                DisplayEntityGroup group = DisplayGroupManager.retrieveDisplayEntityGroup(DisplayGroupManager.LoadMethod.LOCAL, tag);
                if (group == null){
                    p.sendMessage(ChatColor.RED+"- Failed to find saved display entity group in local storage! Checking MongoDB...");
                    attemptAll(p, tag, DisplayGroupManager.LoadMethod.MONGODB);
                    return;
                }
                group.spawn(p.getLocation()).addPlayerSelection(p);
                p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Successfully spawned display entity group at your location! "+ChatColor.WHITE+"(Tagged: "+tag+")");

            }

            case MONGODB -> {
                if (!DisplayEntityPlugin.isMongoEnabled()){
                    p.sendMessage(ChatColor.GRAY+"- MongoDB storage is disabled, checking MYSQL...");
                    attemptAll(p, tag, DisplayGroupManager.LoadMethod.MYSQL);
                    return;
                }
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        DisplayEntityGroup group = DisplayGroupManager.retrieveDisplayEntityGroup(DisplayGroupManager.LoadMethod.MONGODB, tag);
                        if (group == null){
                            p.sendMessage(ChatColor.RED+"- Failed to find saved display entity group in MongoDB database! Checking MYSQL...");
                            attemptAll(p, tag, DisplayGroupManager.LoadMethod.MYSQL);
                            return;
                        }
                        new BukkitRunnable(){
                            @Override
                            public void run() {
                                group.spawn(p.getLocation()).addPlayerSelection(p);
                            }
                        }.runTask(DisplayEntityPlugin.getInstance());

                        p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Successfully spawned display entity group at your location! "+ChatColor.WHITE+"(Tagged: "+tag+")");
                        p.sendMessage(ChatColor.YELLOW+"The spawned group has been selected");
                    }
                }.runTaskAsynchronously(DisplayEntityPlugin.getInstance());
            }

            case MYSQL -> {
                if (!DisplayEntityPlugin.isMYSQLEnabled()){
                    p.sendMessage(ChatColor.GRAY+"- MYSQL storage is disabled.");
                    return;
                }
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        DisplayEntityGroup group = DisplayGroupManager.retrieveDisplayEntityGroup(DisplayGroupManager.LoadMethod.MYSQL, tag);
                        if (group == null){
                            p.sendMessage(ChatColor.RED+"- Failed to find saved display entity group in MYSQL database!");
                            return;
                        }
                        new BukkitRunnable(){
                            @Override
                            public void run() {
                                group.spawn(p.getLocation()).addPlayerSelection(p);
                            }
                        }.runTask(DisplayEntityPlugin.getInstance());
                        p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Successfully spawned display entity group at your location! "+ChatColor.WHITE+"(Tagged: "+tag+")");
                    }
                }.runTaskAsynchronously(DisplayEntityPlugin.getInstance());
            }
        }
    }

    static void save(Player p, String storage){
        SpawnedDisplayEntityGroup spawnedGroup = DisplayGroupManager.getSelectedSpawnedGroup(p);
        if (spawnedGroup == null){
            MainCommand.noSelection(p);
            return;
        }
        if (spawnedGroup.getTag() == null){
            p.sendMessage(ChatColor.RED+"Failed to save display entity group, no tag provided! /mdis group settag <tag>");
            return;
        }
        p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.YELLOW+"Attempting to save spawned display entity group "+ChatColor.WHITE+"(Tagged: "+spawnedGroup.getCleanTag()+")");
        DisplayEntityGroup group = spawnedGroup.toDisplayEntityGroup();
        switch(storage){
            case "all" -> {
                DisplayGroupManager.saveDisplayEntityGroup(DisplayGroupManager.LoadMethod.LOCAL, group, p);
                DisplayGroupManager.saveDisplayEntityGroup(DisplayGroupManager.LoadMethod.MONGODB, group, p);
                DisplayGroupManager.saveDisplayEntityGroup(DisplayGroupManager.LoadMethod.MYSQL, group, p);
            }
            case "local"->{
                DisplayGroupManager.saveDisplayEntityGroup(DisplayGroupManager.LoadMethod.LOCAL, group, p);
            }
            case "mongodb" ->{
                DisplayGroupManager.saveDisplayEntityGroup(DisplayGroupManager.LoadMethod.MONGODB, group, p);
            }
            case "mysql" ->{
                DisplayGroupManager.saveDisplayEntityGroup(DisplayGroupManager.LoadMethod.MYSQL, group, p);
            }
            default ->{
                p.sendMessage(ChatColor.RED+"Invalid storage option!");
            }
        }
    }

    static void delete(Player p, String tag, String storage){
        p.sendMessage(DisplayEntityPlugin.pluginPrefix+ ChatColor.GRAY+"Attempting to delete spawned display entity group "+ChatColor.WHITE+"(Tagged: "+tag+")");
        switch(storage){
            case "all" ->{
                DisplayGroupManager.deleteDisplayEntityGroup(DisplayGroupManager.LoadMethod.LOCAL, tag, p);
                DisplayGroupManager.deleteDisplayEntityGroup(DisplayGroupManager.LoadMethod.MONGODB, tag, p);
                DisplayGroupManager.deleteDisplayEntityGroup(DisplayGroupManager.LoadMethod.MYSQL, tag, p);
            }
            case "local" -> {
                DisplayGroupManager.deleteDisplayEntityGroup(DisplayGroupManager.LoadMethod.LOCAL, tag, p);
            }
            case "mongodb" -> {
                DisplayGroupManager.deleteDisplayEntityGroup(DisplayGroupManager.LoadMethod.MONGODB, tag, p);
            }
            case "mysql" -> {
                DisplayGroupManager.deleteDisplayEntityGroup(DisplayGroupManager.LoadMethod.MYSQL, tag, p);
            }
            default ->{
                p.sendMessage(ChatColor.RED+"Invalid storage option!");
            }
        }
    }
}
