package net.donnypz.displayentityutils.command.anim;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class AnimStopCMD extends PlayerSubCommand {
    AnimStopCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("stop", parentSubCommand, Permission.ANIM_STOP);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            player.sendMessage(Component.text("You must have a group selected to do this animation command!", NamedTextColor.RED));
            return;
        }


        group.stopAnimations(true);
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Stopping all animations played on your selected group!", NamedTextColor.YELLOW)));
    }
}
