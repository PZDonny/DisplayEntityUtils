package net.donnypz.displayentityutils.command.mannequin;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.CMDUtils;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.PivotAxis;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

class MannequinPivotCMD extends PartsSubCommand {
    MannequinPivotCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("pivot", parentSubCommand, Permission.MANNEQUIN_PIVOT, true);
        setTabComplete(2, List.of("x", "y", "z"));
        setTabComplete(3, "<angle>");
        addFlag("-world");
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        PivotAxis axis = CMDUtils.getPivotAxis(args[2], player);
        if (axis == null) return false;

        try{
            float angle = Float.parseFloat(args[3]);
            boolean worldSpace = getOptionalArguments(player, args).hasFlag("-world");
            for (ActivePart p : selection.getSelectedParts()){
                if (p.getType() != SpawnedDisplayEntityPart.PartType.MANNEQUIN) continue;
                p.pivot(angle, axis, worldSpace);
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

        PivotAxis axis = CMDUtils.getPivotAxis(args[2], player);
        if (axis == null) return false;

        try{
            float angle = Float.parseFloat(args[3]);
            boolean worldSpace = getOptionalArguments(player, args).hasFlag("-world");
            selectedPart.pivot(angle, axis, worldSpace);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Pivot applied to mannequin!", NamedTextColor.GREEN)));
            return true;
        }
        catch(IllegalArgumentException e){
            sendIncorrectUsage(player);
            return false;
        }
    }

    @Override
    protected String getDescription() {
        return "Pivot a mannequin around its group's location. " +
                "Pivot around the X (pitch), Y (yaw), or Z (roll) axes" +
                "\nUse \"-world\" to pivot in world space";
    }
}
