package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

class AnimRestoreCMD extends PlayerSubCommand {
    AnimRestoreCMD() {
        super(Permission.ANIM_PREVIEW);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            player.sendMessage(Component.text("You must have a group selected to do this animation command!", NamedTextColor.RED));
            return;
        }

        group.hideFromPlayer(player);
        group.showToPlayer(player);
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Restored group entity data", NamedTextColor.GRAY, TextDecoration.ITALIC)));
        player.sendMessage(Component.text("Animation successfully reversed!", NamedTextColor.GREEN));
    }
}
