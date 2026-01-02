package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.DisplayConfig;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class GroupAutoCullCMD extends GroupSubCommand {
    GroupAutoCullCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("autocull", parentSubCommand, Permission.GROUP_CULLING, 0, false);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected void execute(@NotNull Player player, @NotNull ActiveGroup<?> group, @NotNull String[] args) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Added culling bounds to your selected group!", NamedTextColor.GREEN)));
        group.autoCull(DisplayConfig.widthCullingAdder(), DisplayConfig.heightCullingAdder());
    }
}
