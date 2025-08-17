package net.donnypz.displayentityutils.command.anim;

import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class AnimScaleRespectCMD extends PlayerSubCommand {
    AnimScaleRespectCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("togglescalerespect", parentSubCommand, Permission.ANIM_TOGGLE_SCALE);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (anim == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }

        boolean scaleRespect = !anim.groupScaleRespect();
        anim.groupScaleRespect(!anim.groupScaleRespect());
        player.sendMessage(Component.text("Animation scale respect toggled to: ", NamedTextColor.GREEN)
                .append(Component.text(scaleRespect, NamedTextColor.YELLOW)));
    }
}
