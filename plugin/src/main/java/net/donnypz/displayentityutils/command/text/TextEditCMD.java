package net.donnypz.displayentityutils.command.text;

import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.version.VersionUtils;
import net.donnypz.displayentityutils.utils.dialogs.TextDisplayDialog;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class TextEditCMD extends PartsSubCommand {

    public TextEditCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("edit", parentSubCommand, Permission.TEXT_EDIT);
        addFlag("-&");
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        return false;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (isInvalidType(player, selectedPart, SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY)) return false;

        boolean isLegacy = getOptionalArguments(player, args).hasFlag("-&");
        if (VersionUtils.canViewDialogs(player, true)){
            TextDisplayDialog.sendDialog(player, selectedPart, !isLegacy);
        }
        return false;
    }
}
