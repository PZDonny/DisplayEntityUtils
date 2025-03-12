package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.FramePoint;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.donnypz.displayentityutils.utils.command.FramePointDisplay;
import net.donnypz.displayentityutils.utils.command.RelativePointDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

class AnimRemoveSoundCMD extends PlayerSubCommand {
    AnimRemoveSoundCMD() {
        super(Permission.ANIM_REMOVE_SOUND);
    }

    @Override
    public void execute(Player player, String[] args) {
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

        if (args.length < 3) {
            player.sendMessage(Component.text("Incorrect Usage! /mdis anim removesound <sound | -all>", NamedTextColor.RED));
            return;
        }
        try {
            String soundName = args[2];
            boolean isRemoveAll = soundName.equalsIgnoreCase("-all");
            FramePoint point = (FramePoint) display.getRelativePoint();
            if (isRemoveAll){
                point.removeAllSounds();
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("All Sounds Removed from Frame Point!", NamedTextColor.YELLOW)));
                return;
            }


            if (point.removeSound(soundName)){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Sound Removed from Frame Point!", NamedTextColor.YELLOW)));
            }
            else{
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Sound does not exist in Frame Point!", NamedTextColor.RED)));
            }

        }
        catch (NumberFormatException | IndexOutOfBoundsException e) {
            player.sendMessage(Component.text("Invalid value entered for frame-id! Enter a whole number >= 0", NamedTextColor.RED));
        }
    }
}
