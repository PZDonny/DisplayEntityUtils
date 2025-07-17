package net.donnypz.displayentityutils.command.anim;

import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.List;

class AnimReverseCMD extends PlayerSubCommand {
    AnimReverseCMD() {
        super(Permission.ANIM_REVERSE);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (anim == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }

        List<SpawnedDisplayAnimationFrame> frames = anim.getFrames();
        if (frames.isEmpty()) {
            player.sendMessage(Component.text("Your currently selected animation has no frames!", NamedTextColor.RED));
            player.sendMessage(Component.text("Use \"/mdis anim addframe\" instead", NamedTextColor.GRAY));
            return;
        }
        anim.reverse();
        player.sendMessage(Component.text("Animation successfully reversed!", NamedTextColor.GREEN));
    }
}
