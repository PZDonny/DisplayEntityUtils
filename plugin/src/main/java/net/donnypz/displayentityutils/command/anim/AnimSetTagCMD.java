package net.donnypz.displayentityutils.command.anim;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class AnimSetTagCMD extends PlayerSubCommand {
    AnimSetTagCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("settag", parentSubCommand, Permission.ANIM_SET_TAG);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(Component.text("Incorrect Usage! /mdis anim settag <anim-tag>", NamedTextColor.RED));
            return;
        }

        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (anim == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }
        String tag = args[2];
        anim.setAnimationTag(tag);
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Animation tag successfully set to \"" + tag + "\"", NamedTextColor.GREEN)));
    }
}
