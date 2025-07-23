package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ServerSideSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
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
    protected void executeAllPartsAction(@NotNull Player player, @Nullable SpawnedDisplayEntityGroup group, @NotNull SpawnedPartSelection selection, @NotNull String[] args) {
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Glowing applied to your selection!", NamedTextColor.GREEN)));
        selection.glow();
    }

    @Override
    protected void executeSinglePartAction(@NotNull Player player, @Nullable SpawnedDisplayEntityGroup group, @NotNull ServerSideSelection selection, @NotNull SpawnedDisplayEntityPart selectedPart, @NotNull String[] args) {
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Glowing applied to your selected part!", NamedTextColor.GREEN)));
        selectedPart.glow();
    }
}
