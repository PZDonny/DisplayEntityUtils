package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class GroupSetTagCMD extends GroupSubCommand {
    GroupSetTagCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("settag", parentSubCommand, Permission.GROUP_SETTAG, 3, true);
        setTabComplete(2, "<group-tag>");
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(Component.text("Incorrect Usage! /deu group settag <group-tag>", NamedTextColor.RED));
    }

    @Override
    protected void execute(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull String[] args) {
        String tag = args[2];
        group.setTag(tag);
        player.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Successfully tagged your selected group! <white>(Tagged: "+tag+")")));
    }
}
