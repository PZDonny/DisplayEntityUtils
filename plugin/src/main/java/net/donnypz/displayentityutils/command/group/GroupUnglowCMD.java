package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class GroupUnglowCMD extends GroupSubCommand {
    GroupUnglowCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("unglow", parentSubCommand, Permission.GROUP_GLOW, 0, true);
    }


    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected void execute(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull String[] args) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<yellow>Unglowing selected group!")));
        group.unglow();
    }
}
