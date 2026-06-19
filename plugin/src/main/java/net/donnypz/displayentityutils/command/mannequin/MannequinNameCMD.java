package net.donnypz.displayentityutils.command.mannequin;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.text.TextSetCMD;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class MannequinNameCMD extends PartsSubCommand {
    MannequinNameCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("name", parentSubCommand, Permission.MANNEQUIN_NAME);
        setTabComplete(2, "<name>");
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        return false;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (isInvalidType(player, selectedPart, SpawnedDisplayEntityPart.PartType.MANNEQUIN)) return false;
        String name = TextSetCMD.getTextResult(args);
        selectedPart.setCustomName(LegacyComponentSerializer.legacyAmpersand().deserialize(name));
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Set mannequin name!", NamedTextColor.GREEN)));
        return true;
    }

    @Override
    protected String getDescription() {
        return "Set your selected mannequin's name";
    }
}
