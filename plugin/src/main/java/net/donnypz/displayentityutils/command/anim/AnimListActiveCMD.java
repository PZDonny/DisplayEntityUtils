package net.donnypz.displayentityutils.command.anim;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class AnimListActiveCMD extends PlayerSubCommand {
    AnimListActiveCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("listactive", parentSubCommand, Permission.ANIM_INFO);
    }

    @Override
    public void execute(Player player, String[] args) {
        ActiveGroup<?> group = DisplayGroupManager.getSelectedGroup(player);
        if (group == null) {
            player.sendMessage(Component.text("You must have a group selected to do this animation command!", NamedTextColor.RED));
            return;
        }


        if (!group.isAnimating()){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("This group has no active animations playing!", NamedTextColor.RED)));
            return;
        }

        for (DisplayAnimator animator : group.getActiveAnimators()){
            SpawnedDisplayAnimation anim = animator.getAnimation();
            player.sendMessage(MiniMessage
                    .miniMessage()
                    .deserialize("- Tag: <yellow>"+anim.getAnimationTag()+" <aqua>["+animator.getAnimationType()+"]")
                    .append(Component.text(" [STOP]", NamedTextColor.RED, TextDecoration.BOLD)
                            .clickEvent(ClickEvent.callback(a -> {
                                animator.stop(group);
                                player.sendMessage(Component.text("Animation Stopped!", NamedTextColor.GREEN));
                            }))));
        }
    }
}
