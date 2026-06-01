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
        addFlag("-self");
    }

    @Override
    public void execute(Player player, String[] args) {
        boolean hideForSelf = getOptionalArguments(player, args).hasFlag("-self");

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

        String self = hideForSelf ? " (For self)" : "";
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Hiding all persistent packet-based groups in this chunk."+self, NamedTextColor.YELLOW)));
        if (hideForSelf){
            player.sendMessage(Component.text("| The groups will only be hidden until you are re-sent this chunk", NamedTextColor.GRAY));
        }
    }
}
