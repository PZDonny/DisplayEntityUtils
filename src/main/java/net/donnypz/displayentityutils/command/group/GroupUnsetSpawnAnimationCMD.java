package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

class GroupUnsetSpawnAnimationCMD extends PlayerSubCommand {
    GroupUnsetSpawnAnimationCMD() {
        super(Permission.GROUP_SET_SPAWN_ANIM);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        if (group.getSpawnAnimationTag() == null){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Your selected group does not have a spawn/load animation!", NamedTextColor.RED)));
            return;
        }

        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Successfully unset your group's spawn/load animation!", NamedTextColor.GREEN)));
        group.setSpawnAnimationTag(null, DisplayAnimator.AnimationType.LINEAR, LoadMethod.LOCAL);

    }
}
