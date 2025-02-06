package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

class AnimScaleRespectCMD implements PlayerSubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.ANIM_TOGGLE_SCALE)){
            return;
        }


        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (anim == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }

        boolean scaleRespect = !anim.groupScaleRespect();
        anim.groupScaleRespect(!anim.groupScaleRespect());
        player.sendMessage(Component.text("Animation scale respect toggled to: ", NamedTextColor.GREEN).append(Component.text(scaleRespect, NamedTextColor.YELLOW)));
    }
}
