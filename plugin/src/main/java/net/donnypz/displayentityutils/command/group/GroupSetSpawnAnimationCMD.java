package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

class GroupSetSpawnAnimationCMD extends GroupSubCommand {
    GroupSetSpawnAnimationCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("setspawnanim", parentSubCommand, Permission.GROUP_SET_SPAWN_ANIM, 5, true);
        setTabComplete(2, "<anim-tag>");
        setTabComplete(3, TabSuggestion.STORAGES);
        setTabComplete(4, List.of("linear", "loop"));
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(Component.text("Incorrect Usage! /deu group setspawnanim <animation-tag> <storage> <linear | loop>", NamedTextColor.RED));
        player.sendMessage(Component.text("Valid storage methods are local, mongodb, or mysql", NamedTextColor.GRAY));
        sendAnimationTypes(player);
    }

    @Override
    protected void execute(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull String[] args) {
        LoadMethod loadMethod;
        try{
            loadMethod = LoadMethod.valueOf(args[3].toUpperCase());
        }
        catch(IllegalArgumentException e){
            player.sendMessage(Component.text("Invalid Storage Method!", NamedTextColor.RED));
            player.sendMessage(Component.text("Valid storage methods are local, mongodb, or mysql", NamedTextColor.GRAY));
            return;
        }

        try{
            DisplayAnimator.AnimationType type = DisplayAnimator.AnimationType.valueOf(args[4].toUpperCase());
            group.setSpawnAnimation(args[2], type, loadMethod);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Spawn/Load Animation Set!", NamedTextColor.GREEN)));
            player.sendMessage(Component.text("| [PLAY ANIMATION]", NamedTextColor.AQUA)
                    .hoverEvent(HoverEvent.showText(Component.text("The given animation will only play if it exists!", NamedTextColor.GRAY)))
                    .clickEvent(ClickEvent.callback(a -> {
                        DisplayAPI.getScheduler().runAsync(() -> {
                            if (!group.isRegistered()){
                                a.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("That group is no longer valid!", NamedTextColor.RED)));
                            }
                            if (group.playSpawnAnimation()){
                                a.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Playing animation!", NamedTextColor.GREEN)));
                            }
                            else{
                                a.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Failed to get animation. It may not exist", NamedTextColor.RED)));
                            }
                        });
                    })));
        }
        catch(IllegalArgumentException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid Animation type!", NamedTextColor.RED)));
            sendAnimationTypes(player);
        }
    }

    private void sendAnimationTypes(Player player){
        player.sendMessage(Component.text("- LINEAR: Plays an animation one time then stops", NamedTextColor.GRAY));
        player.sendMessage(Component.text("- LOOP: Plays an animation infinitely until manually stopped", NamedTextColor.GRAY));
    }
}
