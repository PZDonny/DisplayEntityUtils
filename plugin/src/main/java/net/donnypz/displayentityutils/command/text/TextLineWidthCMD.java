package net.donnypz.displayentityutils.command.text;

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

class TextLineWidthCMD extends PartsSubCommand {
    TextLineWidthCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("linewidth", parentSubCommand, Permission.TEXT_SET_LINE_WIDTH, 3, 3);
        setTabComplete(2, "<width>");
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage! /deu text linewidth <width> [-all]", NamedTextColor.RED)));
    }

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        int width = getLineWidth(args, player);
        if (width == -1) return false;
        for (ActivePart part : selection.getSelectedParts()){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
                part.setTextDisplayLineWidth(width);
            }
        }
        player.sendMessage(Component.text("Set text display's line width to "+width+" for ALL selected text displays", NamedTextColor.GREEN));
        return true;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        int width = getLineWidth(args, player);
        if (width == -1) return false;
        if (selectedPart.getType() != SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You can only do this with text display entities", NamedTextColor.RED)));
            return false;
        }
        selectedPart.setTextDisplayLineWidth(width);
        player.sendMessage(Component.text("Set text display's line width to "+width, NamedTextColor.GREEN));
        return true;
    }

    private int getLineWidth(String[] args, Player player){
        try{
            int change = Integer.parseInt(args[2]);
            if (change <= 0){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid width, enter a whole positive number!", NamedTextColor.RED)));
                return -1;
            }

            return change;
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid width, enter a positive whole number!", NamedTextColor.RED)));
            return -1;
        }
    }
}
