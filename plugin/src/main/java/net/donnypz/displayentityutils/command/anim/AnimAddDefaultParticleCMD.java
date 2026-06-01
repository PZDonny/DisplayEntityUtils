package net.donnypz.displayentityutils.command.anim;

import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.FramePoint;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import net.donnypz.displayentityutils.utils.DisplayEntities.particles.AnimationParticleBuilder;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.donnypz.displayentityutils.utils.dialogs.animationparticles.AnimationParticleSelectDialog;
import net.donnypz.displayentityutils.utils.version.VersionUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

class AnimAddDefaultParticleCMD extends PlayerSubCommand {
    AnimAddDefaultParticleCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("adddefaultparticle", parentSubCommand, Permission.ANIM_ADD_PARTICLE);
        setTabComplete(2, List.of("<frame-ids>", "<frame-tag>"));
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (anim == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }

        try{
            Collection<FramePoint> framePoints = DEUCommandUtils.getFrames(player, args[2], anim)
                    .stream()
                    .map(SpawnedDisplayAnimationFrame::getDefaultFramePoint)
                    .toList();

            if (VersionUtils.canViewDialogs(player, false)){
                AnimationParticleSelectDialog.sendDialog(player, framePoints);
            }
            else{
                new AnimationParticleBuilder(player, framePoints);
            }
        }catch(IllegalArgumentException e){}

    }
}