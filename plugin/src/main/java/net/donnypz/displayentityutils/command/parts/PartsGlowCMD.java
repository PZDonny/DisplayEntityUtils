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

class PartsGlowCMD extends PartsSubCommand {
    PartsGlowCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("glow", parentSubCommand, Permission.PARTS_GLOW, 2, 2);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Glowing applied to your selection!", NamedTextColor.GREEN)));
        selection.glow();
        return true;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Glowing applied to your selected part!", NamedTextColor.GREEN)));
        selectedPart.glow();
        return true;
    }
}
