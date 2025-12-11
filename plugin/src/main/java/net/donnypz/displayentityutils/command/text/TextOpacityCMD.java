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
        super("opacity", parentSubCommand, Permission.TEXT_OPACITY, 3, 3);
        setTabComplete(2, "<0-1>");
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage! /deu text opacity <0-1> [-all]", NamedTextColor.RED)));
    }

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        Byte opacity = getOpacity(args, player);
        if (opacity == null) return false;
        for (ActivePart part : selection.getSelectedParts()){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
                part.setTextDisplayTextOpacity(opacity);
            }
        }
        player.sendMessage(DisplayAPI.pluginPrefix
                .append(Component.text("Successfully set text display's opacity to "+opacity+" for ALL selected text displays", NamedTextColor.GREEN)));
        return true;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        Byte opacity = getOpacity(args, player);
        if (opacity == null) return false;
        if (selectedPart.getType() != SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You can only do this with text display entities", NamedTextColor.RED)));
            return false;
        }
        selectedPart.setTextDisplayTextOpacity(opacity);
        player.sendMessage(DisplayAPI.pluginPrefix
                .append(Component.text("Successfully set text display's opacity to "+opacity, NamedTextColor.GREEN)));
        return true;
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
