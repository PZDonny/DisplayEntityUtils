package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.List;

class AnimSetFrameTagCMD implements PlayerSubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.ANIM_SET_FRAME_TAG)){
            return;
        }
        if (args.length < 4) {
            player.sendMessage(Component.text("Incorrect Usage! /mdis anim setframetag <frame-ids> <frame-tag>", NamedTextColor.RED));
            return;
        }

        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (anim == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }

        List<SpawnedDisplayAnimationFrame> frames = anim.getFrames();
        if (frames.isEmpty()) {
            AnimCMD.hasNoFrames(player);
            return;
        }
        String tag = args[3];
        if (!DisplayUtils.isValidTag(tag)){
            DisplayEntityPluginCommand.invalidTag(player, tag);
            return;
        }
        try {
            int[] ids = DEUCommandUtils.commaSeparatedIDs(args[2]);
            //Check if the tag is a number
            try{
                Integer.parseInt(tag);
                player.sendMessage(Component.text("You cannot set the frame tag to a whole number!", NamedTextColor.RED));
                return;
            }
            catch(NumberFormatException ignore){}


            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Frame Tag Set to \"" + tag + "\"", NamedTextColor.GREEN)));
            player.sendMessage(Component.text("Applied To: ", NamedTextColor.YELLOW));
            for (int i : ids){
                try{
                    SpawnedDisplayAnimationFrame frame = frames.get(i);
                    player.sendMessage(Component.text("- "+i, NamedTextColor.GRAY));
                    frame.setTag(tag);
                }
                catch(IndexOutOfBoundsException e){
                    player.sendMessage((Component.text("- "+i+" (Frame doesn't exist)", NamedTextColor.RED)));
                }
            }
        }
        catch (IllegalArgumentException e) {
            player.sendMessage(Component.text("Invalid ID(s) entered! Value(s) must be >= 0", NamedTextColor.RED));
        }
    }
}
