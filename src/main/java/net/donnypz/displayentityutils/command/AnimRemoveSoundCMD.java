package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

class AnimRemoveSoundCMD implements PlayerSubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.ANIM_REMOVE_SOUND)){
            return;
        }

        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (anim == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }
        if (args.length < 5) {
            player.sendMessage(Component.text("Incorrect Usage! /mdis anim removesound <frame-id> <sound | -all> <start | end>", NamedTextColor.RED));
            return;
        }
        try {
            int id = Integer.parseInt(args[2]);
            String soundName = args[3];
            boolean isRemoveAll = soundName.equalsIgnoreCase("-all");
            String placement = args[4];
            SpawnedDisplayAnimationFrame frame = anim.getFrames().get(id);
            boolean success = true;
            if (placement.equalsIgnoreCase("start")){
                if (isRemoveAll){
                    frame.removeAllFrameStartSounds();
                }
                else{
                    success = frame.removeFrameStartSound(soundName);
                }

            }
            else if (placement.equalsIgnoreCase("end")){
                if (isRemoveAll){
                    frame.removeAllFrameEndSounds();
                }
                else{
                    success = frame.removeFrameEndSound(soundName);
                }
            }
            else{
                player.sendMessage(Component.text("Invalid Option! Choose between \"start\" and \"end\"", NamedTextColor.RED));
            }
            if (isRemoveAll){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Removed all sounds from frame's "+placement, NamedTextColor.YELLOW)));
            }
            else{
                if (success){
                    player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Successfully removed sound from frame's "+placement, NamedTextColor.YELLOW)));
                }
                else{
                    player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Sound does not exist for frame's "+placement, NamedTextColor.RED)));
                }
            }
        }
        catch (NumberFormatException | IndexOutOfBoundsException e) {
            player.sendMessage(Component.text("Invalid value entered for frame-id! Enter a whole number >= 0", NamedTextColor.RED));
        }
    }
}
