package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class GroupDespawnCMD extends GroupSubCommand {
    GroupDespawnCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("despawn", parentSubCommand, Permission.GROUP_DESPAWN, 0, false);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected void execute(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull String[] args) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Despawned your selected display entity group!", NamedTextColor.GRAY)));
        if (group instanceof SpawnedDisplayEntityGroup sg){
            sg.unregister(true, true);
        }
        else if (group instanceof PacketDisplayEntityGroup pg){
            if (pg.isPersistent()){
                DisplayGroupManager.removePersistentPacketGroup(pg, true);
            }
            else{
                pg.unregister();
            }
        }
        DisplayGroupManager.deselectGroup(player);
        DisplayEntityPluginCommand.hideRelativePoints(player);
    }
}
