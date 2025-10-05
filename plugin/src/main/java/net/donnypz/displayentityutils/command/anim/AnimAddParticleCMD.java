package net.donnypz.displayentityutils.command.anim;

import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.particles.AnimationParticleBuilder;
import net.donnypz.displayentityutils.utils.VersionUtils;
import net.donnypz.displayentityutils.utils.dialogs.animationparticles.AnimationParticleSelectDialog;
import net.donnypz.displayentityutils.utils.relativepoints.FramePointSelector;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointSelector;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class AnimAddParticleCMD extends PlayerSubCommand {
    AnimAddParticleCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("addparticle", parentSubCommand, Permission.ANIM_ADD_PARTICLE);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (anim == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }

        RelativePointSelector rp = RelativePointUtils.getRelativePointSelector(player);
        if (!(rp instanceof FramePointSelector display)){
            AnimCMD.noFramePointSelection(player);
            return;
        }

        if (VersionUtils.canViewDialogs(player, false)){
            AnimationParticleSelectDialog.sendDialog(player);
        }
        else{
            new AnimationParticleBuilder(player, display.getRelativePoint());
        }
    }
}