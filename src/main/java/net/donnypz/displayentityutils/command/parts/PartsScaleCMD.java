package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.ServerSideSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class PartsScaleCMD extends PartsSubCommand {

    PartsScaleCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("scale", parentSubCommand, Permission.PARTS_TRANSFORM, 4, 4);
    }

    @Override
    protected void executeAllPartsAction(@NotNull Player player, @Nullable SpawnedDisplayEntityGroup group, @NotNull SpawnedPartSelection selection, @NotNull String[] args) {
        for (SpawnedDisplayEntityPart selectedPart : selection.getSelectedParts()){
            if (selectedPart.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
                continue;
            }
            try {
                if (applyScaleChange(getDimension(args), getScale(args), selectedPart, player)) {
                    player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Scale Updated!", NamedTextColor.GREEN)));
                }
            } catch (NumberFormatException e) {
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter a valid number for the scale!", NamedTextColor.RED)));
                return;
            }
        }
    }

    @Override
    protected void executeSinglePartAction(@NotNull Player player, @Nullable SpawnedDisplayEntityGroup group, @NotNull ServerSideSelection selection, @NotNull SpawnedDisplayEntityPart selectedPart, @NotNull String[] args) {
        if (selectedPart.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION) {
            player.sendMessage(Component.text("You cannot do this with an interaction part entity!", NamedTextColor.RED));
            player.sendMessage(Component.text("| Use \"/mdis interaction scale\" instead", NamedTextColor.GRAY));
            return;
        }
        try {
            if (applyScaleChange(getDimension(args), getScale(args), selectedPart, player)) {
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Scale Updated!", NamedTextColor.GREEN)));
            }
        } catch (NumberFormatException e) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter a valid number for the scale!", NamedTextColor.RED)));
        }
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(Component.text("Incorrect Usage! /mdis parts scale <x | y | z | -all> <scale> [-all]", NamedTextColor.RED));
    }

    private String getDimension(String[] args){
        return args[2].toLowerCase();
    }

    private float getScale(String[] args){
        return Float.parseFloat(args[3]);
    }

    private boolean applyScaleChange(String dim, float scale, SpawnedDisplayEntityPart part, Player player){
        switch (dim){
            case "x" -> {
                part.setXScale(scale);
            }
            case "y" -> {
                part.setYScale(scale);
            }
            case "z" -> {
                part.setZScale(scale);
            }
            case "-all" -> {
                part.setScale(scale, scale, scale);
            }
            default -> {
                sendIncorrectUsage(player);
                return false;
            }
        }
        return true;
    }
}
