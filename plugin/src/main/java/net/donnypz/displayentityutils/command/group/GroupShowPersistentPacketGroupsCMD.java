package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class GroupShowPersistentPacketGroupsCMD extends PlayerSubCommand {
    GroupShowPersistentPacketGroupsCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("showpacketgroups", parentSubCommand, Permission.GROUP_CHUNK_PACKET_GROUP_VISIBILITY);
        addFlag("-self");
    }

    @Override
    public void execute(Player player, String[] args) {
        boolean showForSelf = getOptionalArguments(player, args).hasFlag("-self");

        for (PacketDisplayEntityGroup pg : PacketDisplayEntityGroup.getGroups(player.getChunk())){
            if (pg.isPersistent()){
                if (showForSelf){
                    pg.showToPlayer(player, GroupSpawnedEvent.SpawnReason.COMMAND);
                }
                else{
                    pg.setAutoShow(true);
                }
            }
        }

        String self = showForSelf ? " (For self)" : "";
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Showing all persistent, packet-based groups in this chunk."+self, NamedTextColor.GREEN)));
        if (showForSelf){
            player.sendMessage(Component.text("| If the chunks in this group are hidden by default, the groups will be hidden again once you re-sent this chunk", NamedTextColor.GRAY));
        }
    }
}
