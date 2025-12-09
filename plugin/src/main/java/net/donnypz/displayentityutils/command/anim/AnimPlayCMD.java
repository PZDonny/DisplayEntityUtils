package net.donnypz.displayentityutils.command.anim;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class AnimPlayCMD extends PlayerSubCommand {
    AnimPlayCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("play", parentSubCommand, Permission.ANIM_PLAY);
        setTabComplete(2, List.of("-loop", "-packet", "-camera"));
        setTabComplete(3, List.of("-loop", "-packet", "-camera"));
        setTabComplete(4, List.of("-loop", "-packet", "-camera"));
    }

    @Override
    public void execute(Player player, String[] args) {
        ActiveGroup<?> group = DisplayGroupManager.getSelectedGroup(player);
        if (group == null) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You must have a group selected to do this animation command!", NamedTextColor.RED)));
            return;
        }

        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (anim == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }

        if (RelativePointUtils.isViewingRelativePoints(player)){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You cannot play do that while viewing points!", NamedTextColor.RED)));
            return;
        }

        if (!anim.hasFrames()) {
            AnimCMD.hasNoFrames(player);
            return;
        }
        DisplayAnimator.AnimationType animationType = DisplayAnimator.AnimationType.LINEAR;
        boolean packet = false;
        boolean camera = false;
        Component optionResult = Component.empty();
        for (int i = 2; i < args.length; i++){
            String arg = args[i];
            if (arg.equalsIgnoreCase("-loop") && animationType != DisplayAnimator.AnimationType.LOOP){
                animationType = DisplayAnimator.AnimationType.LOOP;
                optionResult = optionResult.append(Component.text(" (LOOPING)", NamedTextColor.YELLOW));
            }
            else if (arg.equalsIgnoreCase("-packet") && !packet){
                packet = true;
                optionResult = optionResult.append(Component.text(" (PACKET-BASED)", NamedTextColor.LIGHT_PURPLE));
            }
            else if (arg.equalsIgnoreCase("-camera") && !camera){
                camera = true;
                optionResult.append(Component.text(" (CAMERA VIEW)", NamedTextColor.AQUA));
            }
        }

        if (animationType == DisplayAnimator.AnimationType.LOOP){
            if (packet){
                DisplayAnimator.playUsingPackets(group, anim, DisplayAnimator.AnimationType.LOOP);
            }
            else{
                group.animateLooping(anim);
            }
        }
        else{
            if (packet){
                DisplayAnimator.playUsingPackets(group, anim, DisplayAnimator.AnimationType.LINEAR);
            }
            else{
                group.animate(anim);
            }
        }
        if (camera){
            DisplayAnimator.playCamera(player, group, anim, animationType);
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Playing Animation!", NamedTextColor.GREEN))
                .append(optionResult));
    }
}
