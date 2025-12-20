package net.donnypz.displayentityutils.command.mannequin;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class MannequinScaleCMD extends PartsSubCommand {
    MannequinScaleCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("scale", parentSubCommand, Permission.MANNEQUIN_SCALE, 3, 3);
        setTabComplete(2, "<scale>");
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage /deu mannequin scale <scale> [-all]", NamedTextColor.RED)));
    }

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        String scaleStr = args[2];
        try{
            float scale = Float.parseFloat(scaleStr);
            for (ActivePart p : selection.getSelectedParts()){
                if (p.getType() != SpawnedDisplayEntityPart.PartType.MANNEQUIN) continue;
                p.setMannequinScale(scale);
            }
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Successfully set scale of ALL selected mannequins!", NamedTextColor.GREEN)));
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
        String scaleStr = args[2];
        try{
            float scale = Float.parseFloat(scaleStr);
            selectedPart.setMannequinScale(scale);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Successfully set mannequin scale!", NamedTextColor.GREEN)));
            return true;
        }
        catch(IllegalArgumentException e){
            sendIncorrectUsage(player);
            return false;
        }
    }
}
