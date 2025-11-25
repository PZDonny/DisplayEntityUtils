package net.donnypz.displayentityutils.command.anim;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class AnimSaveJsonCMD extends PlayerSubCommand {
    AnimSaveJsonCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("savejson", parentSubCommand, Permission.ANIM_SAVE);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayAnimation animation = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (animation == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }

        if (animation.getAnimationTag() == null){
            player.sendMessage(Component.text("Failed to save animation, no tag provided!", NamedTextColor.RED));
            player.sendMessage(Component.text("| Use \"/mdis anim settag <tag>\"", NamedTextColor.GRAY));
            return;
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<yellow> Attempting to save animation as <light_purple>JSON <white>(Tagged:"+animation.getAnimationTag()+")")));
        DisplayAnimation anim = animation.toDisplayAnimation();
        DisplayAPI.getScheduler().runAsync(() -> {
            DisplayAnimationManager.saveDisplayAnimationJson(anim, player);
        });
    }
}
