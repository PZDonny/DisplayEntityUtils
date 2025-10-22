package net.donnypz.displayentityutils.command.anim;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.relativepoints.FramePointSelector;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointSelector;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class AnimMovePointCMD extends PlayerSubCommand {
    AnimMovePointCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("movepoint", parentSubCommand, Permission.ANIM_MOVE_FRAME_POINT);
    }

    @Override
    public void execute(Player player, String[] args) {
        ActiveGroup<?> group = DisplayGroupManager.getSelectedGroup(player);
        if (group == null) {
            player.sendMessage(Component.text("You must have a group selected to do this animation command!", NamedTextColor.RED));
            return;
        }

        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (anim == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }

        RelativePointSelector<?> rp = RelativePointUtils.getRelativePointSelector(player);
        if (!(rp instanceof FramePointSelector)){
            AnimCMD.noFramePointSelection(player);
            return;
        }

        Location pLoc = player.getLocation();
        pLoc.setPitch(0);
        pLoc.setYaw(0);
        rp.setLocation(group, pLoc);
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Successfully moved frame point to your location")));
    }
}
