package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

class AnimNewCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.ANIM_NEW)){
            return;
        }
        SpawnedDisplayAnimation animation = new SpawnedDisplayAnimation();

        DisplayAnimationManager.setSelectedSpawnedAnimation(player, animation);
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("You can now prepare a new animation", NamedTextColor.GREEN)));
        player.sendMessage(Component.text(" - Before animating, if you want your display entity group to retain it's pose, you must save it's current pose " +
                "as a single frame animation", NamedTextColor.GRAY));
    }
}
