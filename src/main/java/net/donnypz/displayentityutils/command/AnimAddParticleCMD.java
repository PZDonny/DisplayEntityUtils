package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.FramePoint;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.particles.AnimationParticleBuilder;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.donnypz.displayentityutils.utils.command.FramePointDisplay;
import net.donnypz.displayentityutils.utils.command.RelativePointDisplay;
import org.bukkit.entity.Player;

class AnimAddParticleCMD implements PlayerSubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.ANIM_ADD_PARTICLE)){
            return;
        }

        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (anim == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }

        RelativePointDisplay rp = DEUCommandUtils.getSelectedRelativePoint(player);
        if (!(rp instanceof FramePointDisplay display)){
            AnimCMD.noFramePointSelection(player);
            return;
        }

        new AnimationParticleBuilder(player, (FramePoint) display.getRelativePoint());
    }
}
