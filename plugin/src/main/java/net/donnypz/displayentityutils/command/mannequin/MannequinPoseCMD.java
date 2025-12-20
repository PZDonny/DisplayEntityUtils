package net.donnypz.displayentityutils.command.mannequin;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.PlaceableGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class MannequinPoseCMD extends PartsSubCommand {
    MannequinPoseCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("pose", parentSubCommand, Permission.MANNEQUIN_POSE, 3, 3);
        setTabComplete(2, TabSuggestion.MANNEQUIN_POSES);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage /deu mannequin pose <pose> [-all]", NamedTextColor.RED)));
    }

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        String poseStr = args[2];
        try{
            Pose pose = Pose.valueOf(poseStr.toUpperCase());
            for (ActivePart p : selection.getSelectedParts()){
                if (p.getType() != SpawnedDisplayEntityPart.PartType.MANNEQUIN) continue;
                p.setMannequinPose(pose);
            }
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Successfully set pose of ALL selected mannequins!", NamedTextColor.GREEN)));
            return true;
        }
        catch(IllegalArgumentException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid Pose!", NamedTextColor.RED)));
            return false;
        }
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (isInvalidType(player, selectedPart, SpawnedDisplayEntityPart.PartType.MANNEQUIN)) return false;
        String poseStr = args[2];
        try{
            Pose pose = Pose.valueOf(poseStr.toUpperCase());
            selectedPart.setMannequinPose(pose);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Successfully set mannequin pose!", NamedTextColor.GREEN)));
            return true;
        }
        catch(IllegalArgumentException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid Pose!", NamedTextColor.RED)));
            return false;
        }
    }
}
