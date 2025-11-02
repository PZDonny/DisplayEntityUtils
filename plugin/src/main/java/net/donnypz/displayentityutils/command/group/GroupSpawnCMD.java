package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.DisplayConfig;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
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
import org.jetbrains.annotations.NotNull;

public class GroupSpawnCMD extends PlayerSubCommand {
    GroupSpawnCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("spawn", parentSubCommand, Permission.GROUP_SPAWN);
        setTabComplete(2, "<group-tag>");
        setTabComplete(3, TabSuggestion.STORAGES);
        setTabComplete(4, "-packet");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(Component.text("Incorrect Usage! /mdis group spawn <group-tag> <storage> [-packet]", NamedTextColor.RED));
            return;
        }
        String tag = args[2];
        String storage = args[3];
        boolean isPacket = args.length > 4 && args[4].equalsIgnoreCase("-packet");
        spawnGroup(player, tag, storage, isPacket);
    }

    private static void spawnGroup(Player p, String tag, String storage, boolean isPacket){
        if (storage.equals("all")){
            p.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Attempting to spawn display entity group from all storage locations", NamedTextColor.YELLOW)));
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
        if (isPacket){
            DisplayGroupManager.addPersistentPacketGroup(spawnLoc, group, true);
            p.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Spawned a <light_purple>packet-based <green>display entity group at your location! <white>(Tagged: "+tag+")")));
        }
        else{
            group.spawn(spawnLoc, GroupSpawnedEvent.SpawnReason.COMMAND);
            p.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Spawned a display entity group at your location! <white>(Tagged: "+tag+")")));
        }
    }


    public static void attemptAll(Player p, String tag, LoadMethod storage, boolean isGroup){
        LoadMethod nextStorage;
        if (storage == LoadMethod.LOCAL){
            nextStorage = LoadMethod.MONGODB;
            if (isGroup){
                p.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<yellow>Attempting to spawn group <white>(Tagged: "+tag+")")));
            }
            else{
                p.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<yellow>Attempting to select animation <white>(Tagged: "+tag+")")));
            }

            if (!DisplayConfig.isLocalEnabled()){
                p.sendMessage(Component.text("- Local storage is disabled, checking MongoDB...", NamedTextColor.GRAY));
                attemptAll(p, tag, nextStorage, isGroup);
                return;
            }
        }
        else if (storage == LoadMethod.MONGODB){
            nextStorage = LoadMethod.MYSQL;
            if (!DisplayConfig.isMongoEnabled()){
                p.sendMessage(Component.text("- MongoDB storage is disabled, checking MYSQL...", NamedTextColor.GRAY));
                attemptAll(p, tag, nextStorage, isGroup);
            }
        }
        else{
            nextStorage = null;
            if (!DisplayConfig.isMYSQLEnabled()){
                p.sendMessage(Component.text("- MYSQL storage is disabled.", NamedTextColor.GRAY));
                return;
            }
        }

        DisplayAPI.getScheduler().runAsync(() -> {
            if (isGroup) {
                DisplayEntityGroup group = DisplayGroupManager.getGroup(storage, tag);
                if (group == null){
                    if (nextStorage != null){
                        p.sendMessage(Component.text("- Failed to find saved display entity group in "+storage.getDisplayName()+" database! Checking "+nextStorage.getDisplayName()+"...", NamedTextColor.RED));
                        attemptAll(p, tag, nextStorage, true);
                    }
                    return;
                }

                p.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Successfully spawned display entity group at your location! <white>(Tagged: "+tag+")")));
                Location spawnLoc = p.getLocation();

                DisplayAPI.getScheduler().run(() -> {
                    if (!spawnLoc.isChunkLoaded()){
                        Bukkit.getConsoleSender().sendMessage(Component.text("Failed to spawn group in unloaded chunk", NamedTextColor.RED));
                        return;
                    }

                    SpawnedDisplayEntityGroup g = group.spawn(spawnLoc, GroupSpawnedEvent.SpawnReason.COMMAND);
                    if (p.isConnected() && DisplayConfig.autoSelectGroups()){
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
                p.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Successfully selected display animation! <white>(Tagged: "+tag+")")));
            }
        });
    }
}
