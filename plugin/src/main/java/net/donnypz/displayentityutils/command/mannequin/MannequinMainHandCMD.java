package net.donnypz.displayentityutils.command.mannequin;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MainHand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

class MannequinMainHandCMD extends PartsSubCommand {
    MannequinMainHandCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("mainhand", parentSubCommand, Permission.MANNEQUIN_POSE, 3, 3);
        setTabComplete(2, List.of("left", "right"));
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage /deu mannequin mainhand <left | right> [-all]", NamedTextColor.RED)));
    }

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        String handStr = args[2];
        try{
            MainHand hand = MainHand.valueOf(handStr);
            for (ActivePart p : selection.getSelectedParts()){
                if (p.getType() != SpawnedDisplayEntityPart.PartType.MANNEQUIN) continue;
                p.setMannequinMainHand(hand);
            }
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Set main hand of ALL selected mannequins!", NamedTextColor.GREEN)));
            return true;
        }
        catch(IllegalArgumentException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid Main Hand!", NamedTextColor.RED)));
            return false;
        }
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (isInvalidType(player, selectedPart, SpawnedDisplayEntityPart.PartType.MANNEQUIN)) return false;

        String handStr = args[2];
        try{
            MainHand hand = MainHand.valueOf(handStr);
            selectedPart.setMannequinMainHand(hand);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Set mannequin's main hand!", NamedTextColor.GREEN)));
            return true;
        }
        catch(IllegalArgumentException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid Main Hand!", NamedTextColor.RED)));
            return false;
        }
    }
}
