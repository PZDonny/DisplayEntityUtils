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

class TextOpacityCMD extends PartsSubCommand {
    TextOpacityCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("opacity", parentSubCommand, Permission.TEXT_OPACITY, 3, 4);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage! /mdis text opacity <0-1> [-all]", NamedTextColor.RED)));
    }

    @Override
    protected void executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        Byte opacity = getOpacity(args, player);
        if (opacity == null) return;
        for (ActivePart part : selection.getSelectedParts()){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
                part.setTextDisplayTextOpacity(opacity);
            }
        }
        player.sendMessage(DisplayAPI.pluginPrefix
                .append(Component.text("Successfully set text display's opacity to "+opacity+" for ALL selected text displays", NamedTextColor.GREEN)));
    }

    @Override
    protected void executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        Byte opacity = getOpacity(args, player);
        if (opacity == null) return;
        if (selectedPart.getType() != SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You can only do this with text display entities", NamedTextColor.RED)));
            return;
        }
        selectedPart.setTextDisplayTextOpacity(opacity);
        player.sendMessage(DisplayAPI.pluginPrefix
                .append(Component.text("Successfully set text display's opacity to "+opacity, NamedTextColor.GREEN)));
    }

    private Byte getOpacity(String[] args, Player player){
        try{
            double change = Double.parseDouble(args[2]);
            if (change < 0 || change > 1){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid opacity, enter a value between 0 and 1!", NamedTextColor.RED)));
                return null;
            }
            double percentage = 255*change;
            if (percentage >= 25.5 && percentage < 26){//0.1 input range
                percentage = 26;
            }
            byte opacity = (byte) percentage;
            if (opacity > -1 && opacity < 26){ //Adjusted for Minecraft Shader Values (Outside of 0.1 range)
                opacity = 25;
            }
            return opacity;
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid opacity, enter a value between 0 and 1!", NamedTextColor.RED)));
            return null;
        }
    }
}
