package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.managers.PluginFolders;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class GroupSpawnJSONCMD extends PlayerSubCommand {
    GroupSpawnJSONCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("spawnjson", parentSubCommand, Permission.GROUP_SPAWN);
        setTabComplete(2, "<file-name>");
        setTabComplete(3, "-packet");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(Component.text("Incorrect Usage! /deu group spawnjson <file-name> [-packet]", NamedTextColor.RED));
            return;
        }
        String tag = args[2];
        boolean isPacket = args.length > 3 && args[3].equalsIgnoreCase("-packet");
        spawnGroup(player, tag, isPacket);
    }

    private static void spawnGroup(Player p, String tag, boolean isPacket){
        String fileName = tag.endsWith(".json") ? tag : tag+".json";
        DisplayEntityGroup group = DisplayGroupManager.getGroupFromJson(new File(PluginFolders.groupSaveFolder, "/"+fileName));
        if (group == null){
            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>- Failed to find <light_purple>JSON <red>saved display entity group!"));
            return;
        }

        Location spawnLoc = p.getLocation();
        if (isPacket){
            DisplayGroupManager.addPersistentPacketGroup(spawnLoc, group, true, GroupSpawnedEvent.SpawnReason.COMMAND);
            p.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Spawned a <light_purple>packet-based <green>display entity group at your location! <white>(Tagged: "+tag+")")));
        }
        else{
            group.spawn(spawnLoc, GroupSpawnedEvent.SpawnReason.COMMAND);
            p.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Spawned a display entity group at your location! <white>(Tagged: "+tag+")")));
        }
    }
}
