package net.donnypz.displayentityutils.command.display;

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

import java.util.List;

class DisplayScaleCMD extends PartsSubCommand {

    DisplayScaleCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("scale", parentSubCommand, Permission.PARTS_TRANSFORM, 4, 4);
        setTabComplete(2, List.of("x", "y", "z", "-all"));
        setTabComplete(3, "<scale>");
    }

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        for (ActivePart selectedPart : selection.getSelectedParts()){
            if (!selectedPart.isDisplay()){
                continue;
            }
            try {
                if (applyScaleChange(getDimension(args), getScale(args), selectedPart, player)) {
                    player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Scale updated for selected display parts!", NamedTextColor.GREEN)));
                }
            } catch (NumberFormatException e) {
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a valid number for the scale!", NamedTextColor.RED)));
                return false;
            }
        }
        return true;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (isNotDisplay(player, selectedPart)) return false;
        try {
            if (applyScaleChange(getDimension(args), getScale(args), selectedPart, player)) {
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Scale updated!", NamedTextColor.GREEN)));
                return true;
            }
            else{
                return false;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a valid number for the scale!", NamedTextColor.RED)));
            return false;
        }
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(Component.text("Incorrect Usage! /deu display scale <x | y | z | -all> <scale> [-all]", NamedTextColor.RED));
    }

    private String getDimension(String[] args){
        return args[2].toLowerCase();
    }

    private float getScale(String[] args){
        return Float.parseFloat(args[3]);
    }

    private boolean applyScaleChange(String dim, float scale, ActivePart part, Player player){
        switch (dim){
            case "x" -> {
                part.setDisplayXScale(scale);
            }
            case "y" -> {
                part.setDisplayYScale(scale);
            }
            case "z" -> {
                part.setDisplayZScale(scale);
            }
            case "-all" -> {
                part.setDisplayScale(scale, scale, scale);
            }
            default -> {
                sendIncorrectUsage(player);
                return false;
            }
        }
        return true;
    }
}
