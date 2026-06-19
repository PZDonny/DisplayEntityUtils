package net.donnypz.displayentityutils.command.mannequin;

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

class MannequinUnnameCMD extends PartsSubCommand {
    MannequinUnnameCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("unname", parentSubCommand, Permission.MANNEQUIN_NAME, true);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        for (ActivePart p : selection.getSelectedParts()){
            if (p.getType() == SpawnedDisplayEntityPart.PartType.MANNEQUIN){
                p.setCustomName(null);
            }
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Name removed for ALL selected mannequins!", NamedTextColor.YELLOW)));
        return false;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (isInvalidType(player, selectedPart, SpawnedDisplayEntityPart.PartType.MANNEQUIN)) return false;
        selectedPart.setCustomName(null);
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Your selected mannequin's name has been removed!", NamedTextColor.YELLOW)));
        return true;
    }

    @Override
    protected String getDescription() {
        return "Remove your selected mannequin's name";
    }
}
