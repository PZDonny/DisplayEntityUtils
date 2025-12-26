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

class MannequinPivotCMD extends PartsSubCommand {
    MannequinPivotCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("pivot", parentSubCommand, Permission.MANNEQUIN_PIVOT, 3, 3);
        setTabComplete(2, "<angle>");
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage /deu mannequin pivot <angle> [-all]", NamedTextColor.RED)));
    }

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        try{
            float angle = Float.parseFloat(args[2]);
            for (ActivePart p : selection.getSelectedParts()){
                if (p.getType() != SpawnedDisplayEntityPart.PartType.MANNEQUIN) continue;
                p.pivot(angle);
            }
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Pivoted ALL selected mannequins!", NamedTextColor.GREEN)));
            return true;
        }
        catch(IllegalArgumentException e){
            sendIncorrectUsage(player);
            return false;
        }
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (isInvalidType(player, selectedPart, SpawnedDisplayEntityPart.PartType.MANNEQUIN)) return false;
        try{
            float angle = Float.parseFloat(args[2]);
            selectedPart.pivot(angle);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Pivot applied to mannequin!", NamedTextColor.GREEN)));
            return true;
        }
        catch(IllegalArgumentException e){
            sendIncorrectUsage(player);
            return false;
        }
    }
}
