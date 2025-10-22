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
        setTabComplete(2, "-self");
    }

    @Override
    public void execute(Player player, String[] args) {
        boolean showForSelf = args.length > 2 && args[2].equalsIgnoreCase("-self");

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
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Showing all persistent, packet-based groups in this chunk.", NamedTextColor.GREEN)));
        if (showForSelf) player.sendMessage(Component.text("For only you, and if the groups are hidden for others, the groups are only revealed until you are re-sent this chunk"));
    }
}
