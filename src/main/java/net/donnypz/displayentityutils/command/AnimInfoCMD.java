package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

class AnimInfoCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.ANIM_INFO)){
            return;
        }
        SpawnedDisplayAnimation animation = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (animation == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }

        player.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
        String animTag = animation.getAnimationTag() == null ? ChatColor.RED + "NOT SET" : ChatColor.YELLOW + animation.getAnimationTag();

        player.sendMessage("Animation Tag: " + ChatColor.YELLOW + animTag);
        if (!animation.isPartAnimation()) {
            player.sendMessage("Animation Part Tag: " + ChatColor.RED + "NOT SET");
        } else {
            player.sendMessage("Animation Part Tag: " + ChatColor.YELLOW + animation.getPartTag());
        }
        player.sendMessage("Total Frames: " + ChatColor.YELLOW + animation.getFrames().size());
        player.sendMessage("Total Duration: "+ChatColor.YELLOW+animation.getDuration()+" ticks");
        player.sendMessage("Respect Scale: " + ChatColor.YELLOW + animation.groupScaleRespect());
    }
}
