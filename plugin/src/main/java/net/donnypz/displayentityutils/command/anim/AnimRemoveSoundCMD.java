package net.donnypz.displayentityutils.command.anim;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.FramePoint;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.relativepoints.FramePointSelector;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointSelector;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class AnimRemoveSoundCMD extends PlayerSubCommand {
    AnimRemoveSoundCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("removesound", parentSubCommand, Permission.ANIM_REMOVE_SOUND);
        setTabComplete(2, List.of("<sound>", "-all"));
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (anim == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }

        RelativePointSelector<?> rp = RelativePointUtils.getRelativePointSelector(player);
        if (!(rp instanceof FramePointSelector display)){
            AnimCMD.noFramePointSelection(player);
            return;
        }

        if (args.length < 3) {
            player.sendMessage(Component.text("Incorrect Usage! /deu anim removesound <sound | -all>", NamedTextColor.RED));
            return;
        }
        try {
            String soundName = args[2];
            boolean isRemoveAll = soundName.equalsIgnoreCase("-all");
            FramePoint point = display.getRelativePoint();
            if (isRemoveAll){
                point.removeAllSounds();
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("All Sounds Removed from Frame Point!", NamedTextColor.YELLOW)));
                return;
            }


            if (point.removeSound(soundName)){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Sound Removed from Frame Point!", NamedTextColor.YELLOW)));
            }
            else{
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Sound does not exist in Frame Point!", NamedTextColor.RED)));
            }

        }
        catch (NumberFormatException | IndexOutOfBoundsException e) {
            player.sendMessage(Component.text("Invalid value entered for frame-id! Enter a whole number >= 0", NamedTextColor.RED));
        }
    }
}
