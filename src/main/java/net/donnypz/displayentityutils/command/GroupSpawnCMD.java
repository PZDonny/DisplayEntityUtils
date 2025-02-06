package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

class GroupSpawnCMD implements PlayerSubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.GROUP_SPAWN)){
            return;
        }


        if (args.length < 4) {
            player.sendMessage(Component.text("Incorrect Usage! /mdis group spawn <group-tag> <storage>", NamedTextColor.RED));
            return;
        }
        String tag = args[2];
        String storage = args[3];
        spawnGroup(player, tag, storage);
    }

    private static void spawnGroup(Player p, String tag, String storage){
        if (storage.equals("all")){
            p.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Attempting to spawn display entity group from all storage locations", NamedTextColor.YELLOW)));
            attemptAll(p, tag, LoadMethod.LOCAL, true);
            return;
        }

        LoadMethod loadMethod;
        try{
            loadMethod = LoadMethod.valueOf(storage.toUpperCase());
        }
        catch(IllegalArgumentException e){
            p.sendMessage(Component.text("Invalid Storage Method!", NamedTextColor.RED));
            p.sendMessage(Component.text("Valid storage methods are local, mongodb, or mysql", NamedTextColor.GRAY));
            return;
        }

        if (!loadMethod.isEnabled()){
            p.sendMessage(Component.text("- Storage location is disabled and cannot be checked!", NamedTextColor.GRAY));
            return;
        }
        DisplayEntityGroup group = DisplayGroupManager.getGroup(loadMethod, tag);
        if (group == null){
            p.sendMessage(Component.text("- Failed to find saved display entity group in that storage location!", NamedTextColor.RED));
            return;
        }
        Location spawnLoc = p.getLocation();
        group.spawn(spawnLoc, GroupSpawnedEvent.SpawnReason.COMMAND);


        p.sendMessage(DisplayEntityPlugin.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Successfully spawned display entity group at your location! <white>(Tagged: "+tag+")")));
    }


    static void attemptAll(Player p, String tag, LoadMethod storage, boolean isGroup){
        LoadMethod nextStorage;
        if (storage == LoadMethod.LOCAL){
            nextStorage = LoadMethod.MONGODB;
            p.sendMessage(DisplayEntityPlugin.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<yellow>Attempting to spawn group <white>(Tagged: "+tag+")")));
            if (!DisplayEntityPlugin.isLocalEnabled()){
                p.sendMessage(Component.text("- Local storage is disabled, checking MongoDB...", NamedTextColor.GRAY));
                attemptAll(p, tag, nextStorage, isGroup);
                return;
            }
        }
        else if (storage == LoadMethod.MONGODB){
            nextStorage = LoadMethod.MYSQL;
            if (!DisplayEntityPlugin.isMongoEnabled()){
                p.sendMessage(Component.text("- MongoDB storage is disabled, checking MYSQL...", NamedTextColor.GRAY));
                attemptAll(p, tag, nextStorage, isGroup);
            }
        }
        else{
            nextStorage = null;
            if (!DisplayEntityPlugin.isMYSQLEnabled()){
                p.sendMessage(Component.text("- MYSQL storage is disabled.", NamedTextColor.GRAY));
                return;
            }
        }

        Bukkit.getScheduler().runTaskAsynchronously(DisplayEntityPlugin.getInstance(), () -> {
            if (isGroup) {
                DisplayEntityGroup group = DisplayGroupManager.getGroup(storage, tag);
                if (group == null){
                    if (nextStorage != null){
                        p.sendMessage(Component.text("- Failed to find saved display entity group in "+storage.getDisplayName()+" database! Checking "+nextStorage.getDisplayName()+"...", NamedTextColor.RED));
                        attemptAll(p, tag, nextStorage, true);
                    }
                    return;
                }

                p.sendMessage(DisplayEntityPlugin.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Successfully spawned display entity group at your location! <white>(Tagged: "+tag+")")));
                Location spawnLoc = p.getLocation();

                Bukkit.getScheduler().runTask(DisplayEntityPlugin.getInstance(), () -> {
                    if (!spawnLoc.isChunkLoaded()){
                        Bukkit.getConsoleSender().sendMessage(Component.text("Failed to spawn group in unloaded chunk", NamedTextColor.RED));
                        return;
                    }

                    SpawnedDisplayEntityGroup g = group.spawn(spawnLoc, GroupSpawnedEvent.SpawnReason.COMMAND);
                    if (p.isConnected() && DisplayEntityPlugin.autoSelectGroups()){
                        g.addPlayerSelection(p);
                        p.sendMessage(Component.text("Spawned group has been automatically selected", NamedTextColor.GRAY));
                    }
                });
            }
            else{
                DisplayAnimation anim = DisplayAnimationManager.getAnimation(LoadMethod.LOCAL, tag);
                if (anim == null){
                    if (nextStorage != null){
                        p.sendMessage(Component.text("- Failed to find saved display animation in "+storage.getDisplayName()+" database! Checking "+nextStorage.getDisplayName()+"...", NamedTextColor.RED));
                        attemptAll(p, tag, nextStorage, false);
                    }
                    return;
                }
                DisplayAnimationManager.setSelectedSpawnedAnimation(p, anim.toSpawnedDisplayAnimation());
                p.sendMessage(DisplayEntityPlugin.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Successfully selected display animation! <white>(Tagged: "+tag+")")));
            }
        });
    }
}
