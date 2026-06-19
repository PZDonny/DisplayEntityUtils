package net.donnypz.displayentityutils.command.mannequin;

import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.mannequin.ui.MannequinEquipmentGUI;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class MannequinEquipmentCMD extends PartsSubCommand {
    MannequinEquipmentCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("equipment", parentSubCommand, Permission.MANNEQUIN_SET_EQUIPMENT);
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
        MannequinEquipmentGUI.edit(player, selectedPart);
        return true;
    }
}
