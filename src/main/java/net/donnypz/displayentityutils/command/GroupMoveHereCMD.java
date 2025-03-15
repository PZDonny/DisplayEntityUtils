package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

class GroupMoveHereCMD extends PlayerSubCommand {
    GroupMoveHereCMD() {
        super(Permission.GROUP_TRANSFORM);
    }

    @Override
    public void execute(Player player, String[] args) {

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        if (DEUCommandUtils.isViewingRelativePoints(player)){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("You cannot play do that while viewing points!", NamedTextColor.RED)));
            return;
        }

        boolean result = group.teleport(player.getLocation(), true);
        if (!result){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Failed to move spawned display entity group to your location", NamedTextColor.RED)));
            return;
        }
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Moved spawned group to your location!", NamedTextColor.GREEN)));
    }
}
