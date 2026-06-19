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

class AnimPlayCMD extends PlayerSubCommand {
    AnimPlayCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("play", parentSubCommand, Permission.ANIM_PLAY);
        addFlag("-loop");
        addFlag("-packet");
        addFlag("-camera");
        addFlag("-nodata");
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

        OptionalArguments optionalArgs = getOptionalArguments(player, args);
        DisplayAnimator.AnimationType animationType = optionalArgs.hasFlag("-loop")
                ? DisplayAnimator.AnimationType.LOOP
                : DisplayAnimator.AnimationType.LINEAR;

        Component optionResultComp = Component.empty();

        boolean packet = optionalArgs.hasFlag("-packet");
        boolean camera = optionalArgs.hasFlag("-camera");
        boolean dataChange = !optionalArgs.hasFlag("-nodata");

        if (packet) optionResultComp = optionResultComp.append(Component.text(" (PACKET-BASED)", NamedTextColor.LIGHT_PURPLE));
        if (!dataChange) optionResultComp = optionResultComp.append(Component.text(" (NO DATA CHANGES)", NamedTextColor.GOLD));

        if (animationType == DisplayAnimator.AnimationType.LOOP){
            optionResultComp = optionResultComp.append(Component.text(" (LOOPING)", NamedTextColor.YELLOW));
            if (packet){
                DisplayAnimator.playUsingPackets(group, anim, DisplayAnimator.AnimationType.LOOP, dataChange);
            }
            else{
                group.animateLooping(anim, dataChange);
            }
        }
        else{
            if (packet){
                DisplayAnimator.playUsingPackets(group, anim, DisplayAnimator.AnimationType.LINEAR, dataChange);
            }
            else{
                group.animate(anim, dataChange);
            }
        }
        if (camera){
            DisplayAnimator.playCamera(player, group, anim, animationType);
            optionResultComp = optionResultComp.append(Component.text(" (CAMERA VIEW)", NamedTextColor.AQUA));
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Playing Animation!", NamedTextColor.GREEN))
                .append(optionResultComp));
    }

    @Override
    protected String getDescription() {
        return "Play your selected animation on your selected group." +
                " \n\"-loop\" will make the animation loop." +
                " \n\"-packet\" will play the animation using packets." +
                " \n\"-camera\" will set your view to the animation's camera, if present" +
                " \n\"-nodata\" will disable texture changes for item/block displays and text display text changes";
    }
}
