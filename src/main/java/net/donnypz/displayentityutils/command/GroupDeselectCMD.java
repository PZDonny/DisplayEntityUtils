package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

class GroupDeselectCMD extends PlayerSubCommand {
    GroupDeselectCMD() {
        super(Permission.GROUP_SELECT);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Your group selection is already cleared!", NamedTextColor.YELLOW)));
            return;
        }

        DisplayGroupManager.deselectSpawnedGroup(player);
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Group selection cleared!", NamedTextColor.GREEN)));
    }
}
