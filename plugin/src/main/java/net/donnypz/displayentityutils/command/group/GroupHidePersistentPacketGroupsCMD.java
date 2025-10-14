package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class GroupHidePersistentPacketGroupsCMD extends PlayerSubCommand {
    GroupHidePersistentPacketGroupsCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("hidepacketgroups", parentSubCommand, Permission.GROUP_CHUNK_PACKET_GROUP_VISIBILITY);
    }

    @Override
    public void execute(Player player, String[] args) {
        boolean hideForSelf = args.length > 2 && args[2].equalsIgnoreCase("-self");

        for (PacketDisplayEntityGroup pg : PacketDisplayEntityGroup.getGroups(player.getChunk())){
            if (pg.isPersistent()){
                if (hideForSelf){
                    pg.hideFromPlayer(player);
                }
                else{
                    pg.setAutoShow(false);
                    pg.hide();
                }
            }
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Hiding all persistent packet-based groups in this chunk.", NamedTextColor.YELLOW)));
        if (hideForSelf) player.sendMessage(Component.text("For only you, the groups are only hidden until you are re-sent this chunk"));
    }
}
