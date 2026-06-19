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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GroupSpawnCMD extends PlayerSubCommand {
    GroupSpawnCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("spawn", parentSubCommand, Permission.GROUP_SPAWN);
        setTabComplete(2, "<group-tag>");
        setTabComplete(3, TabSuggestion.STORAGES);
        addFlag("-packet");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!hasMinimumArguments(player, args)) return;

        String tag = args[2];
        String storage = args[3];

        boolean isPacket = getOptionalArguments(player, args).hasFlag("-packet");
        spawnGroup(player, player.getLocation(), tag, storage, isPacket);
    }

    static void spawnGroup(CommandSender sender, Location spawnLoc, String tag, String storage, boolean isPacket){
        if (storage.equals("all")){
            sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Attempting to spawn display entity group from all storage locations", NamedTextColor.YELLOW)));
            attemptAll(sender, spawnLoc, tag, LoadMethod.LOCAL, true);
            return;
        }

        LoadMethod loadMethod;
        try{
            loadMethod = LoadMethod.valueOf(storage.toUpperCase());
        }
        catch(IllegalArgumentException e){
            sender.sendMessage(Component.text("Invalid Storage Method!", NamedTextColor.RED));
            sender.sendMessage(Component.text("Valid storage methods are local, mongodb, or mysql", NamedTextColor.GRAY));
            return;
        }

        if (!loadMethod.isEnabled()){
            sender.sendMessage(Component.text("- Storage location is disabled and cannot be checked!", NamedTextColor.GRAY));
            return;
        }
        DisplayEntityGroup group = DisplayGroupManager.getGroup(loadMethod, tag);
        if (group == null){
            sender.sendMessage(Component.text("- Failed to find saved display entity group in that storage location!", NamedTextColor.RED));
            return;
        }

        if (isPacket){
            DisplayGroupManager.addPersistentPacketGroup(spawnLoc, group, true, GroupSpawnedEvent.SpawnReason.COMMAND);
            sender.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Spawned a <light_purple>packet-based <green>display entity group at your location! <white>(Tagged: "+tag+")")));
        }
        else{
            group.spawn(spawnLoc, GroupSpawnedEvent.SpawnReason.COMMAND);
            sender.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Spawned a display entity group at your location! <white>(Tagged: "+tag+")")));
        }
    }


    public static void attemptAll(CommandSender sender, Location spawnLoc, String tag, LoadMethod storage, boolean isGroup){
        LoadMethod nextStorage;
        if (storage == LoadMethod.LOCAL){
            nextStorage = LoadMethod.MONGODB;
            if (isGroup){
                sender.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<yellow>Attempting to spawn group <white>(Tagged: "+tag+")")));
            }
            else{
                sender.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<yellow>Attempting to select animation <white>(Tagged: "+tag+")")));
            }

            if (!DisplayConfig.isLocalEnabled()){
                sender.sendMessage(Component.text("- Local storage is disabled, checking MongoDB...", NamedTextColor.GRAY));
                attemptAll(sender, spawnLoc, tag, nextStorage, isGroup);
                return;
            }
        }
        else if (storage == LoadMethod.MONGODB){
            nextStorage = LoadMethod.MYSQL;
            if (!DisplayConfig.isMongoEnabled()){
                sender.sendMessage(Component.text("- MongoDB storage is disabled, checking MYSQL...", NamedTextColor.GRAY));
                attemptAll(sender, spawnLoc, tag, nextStorage, isGroup);
            }
        }
        else{
            nextStorage = null;
            if (!DisplayConfig.isMYSQLEnabled()){
                sender.sendMessage(Component.text("- MYSQL storage is disabled.", NamedTextColor.GRAY));
                return;
            }
        }

        DisplayAPI.getScheduler().runAsync(() -> {
            if (isGroup) {
                DisplayEntityGroup group = DisplayGroupManager.getGroup(storage, tag);
                if (group == null){
                    if (nextStorage != null){
                        sender.sendMessage(Component.text("- Failed to find saved display entity group in "+storage.getDisplayName()+" database! Checking "+nextStorage.getDisplayName()+"...", NamedTextColor.RED));
                        attemptAll(sender, spawnLoc, tag, nextStorage, true);
                    }
                    return;
                }

                sender.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Successfully spawned display entity group at your location! <white>(Tagged: "+tag+")")));

                DisplayAPI.getScheduler().run(() -> {
                    if (!spawnLoc.isChunkLoaded()){
                        Bukkit.getConsoleSender().sendMessage(Component.text("Failed to spawn group in unloaded chunk", NamedTextColor.RED));
                        return;
                    }

                    SpawnedDisplayEntityGroup g = group.spawn(spawnLoc, GroupSpawnedEvent.SpawnReason.COMMAND);
                    if (sender instanceof Player player){
                        if (player.isConnected() && DisplayConfig.autoSelectGroups()){
                            g.addPlayerSelection(player);
                            sender.sendMessage(Component.text("Spawned group has been automatically selected", NamedTextColor.GRAY));
                        }
                    }

                });
            }
            else{
                DisplayAnimation anim = DisplayAnimationManager.getAnimation(LoadMethod.LOCAL, tag);
                if (anim == null){
                    if (nextStorage != null){
                        sender.sendMessage(Component.text("- Failed to find saved display animation in "+storage.getDisplayName()+" database! Checking "+nextStorage.getDisplayName()+"...", NamedTextColor.RED));
                        attemptAll(sender, spawnLoc, tag, nextStorage, false);
                    }
                    return;
                }
                if (sender instanceof Player player){
                    DisplayAnimationManager.setSelectedSpawnedAnimation(player, anim.toSpawnedDisplayAnimation());
                    sender.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Animation selected! <white>(Tagged: "+tag+")")));
                }
            }
        });
    }
}
