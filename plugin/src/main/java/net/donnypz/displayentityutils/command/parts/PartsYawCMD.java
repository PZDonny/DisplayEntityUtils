package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class PartsYawCMD extends PartsSubCommand {

    PartsYawCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("yaw", parentSubCommand, Permission.PARTS_TRANSFORM, 3, 0);
        setTabComplete(2, "<yaw>");
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(Component.text("Incorrect Usage! /deu parts yaw <yaw>", NamedTextColor.RED));
    }

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        return false;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        try{
            float yaw = Float.parseFloat(args[2]);
            double oldYaw = selectedPart.getYaw();
            selectedPart.setYaw(yaw, false);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Yaw set!", NamedTextColor.GREEN)));
            player.sendMessage(Component.text("| Old Yaw: "+oldYaw, NamedTextColor.GRAY));
            return true;
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a valid number for the yaw!", NamedTextColor.RED)));
            return false;
        }
    }
}
