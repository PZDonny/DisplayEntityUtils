package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class PartsUnglowCMD extends PartsSubCommand {
    PartsUnglowCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("unglow", parentSubCommand, Permission.PARTS_GLOW, 2, 2);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected void executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Removed the glow from your selection!", NamedTextColor.YELLOW)));
        selection.unglow();
    }

    @Override
    protected void executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Removed the glow from your selected part!", NamedTextColor.YELLOW)));
        selectedPart.unglow();
    }
}