package net.donnypz.displayentityutils.command.text;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.ConversionUtils;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class TextBackgroundCMD extends PartsSubCommand {
    TextBackgroundCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("background", parentSubCommand, Permission.TEXT_BACKGROUND, 4, 4);
        setTabComplete(2, TabSuggestion.COLORS);
        setTabComplete(3, "<0-1>");
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage! /deu text background <color | hex-code> <0-1> [-all]", NamedTextColor.RED)));
    }

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        Color color = getColor(args, player);
        if (color == null) return false;
        for (ActivePart part : selection.getSelectedParts()){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
                part.setTextDisplayBackgroundColor(color);
            }
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Set background color for ALL selected text displays", NamedTextColor.GREEN)));
        return true;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        Color color = getColor(args, player);
        if (color == null) return false;
        if (selectedPart.getType() != SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You can only do this with text display entities", NamedTextColor.RED)));
            return false;
        }
        selectedPart.setTextDisplayBackgroundColor(color);
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Set text display's background color", NamedTextColor.GREEN)));
        return true;
    }

    private Color getColor(String[] args, Player player){
        Color c = ConversionUtils.getColorFromText(args[2]);
        if (c == null){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a valid color!", NamedTextColor.RED)));
            player.sendMessage(Component.text("/deu text background <color | hex-code> <0-1>", NamedTextColor.GRAY));
            return null;
        }
        try{
            double change = Double.parseDouble(args[3]);
            if (change < 0 || change > 1){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid opacity, enter a value between 0 and 1!", NamedTextColor.RED)));
                return null;
            }
            double multiplied = 255*change;
            if (multiplied >= 25.5 && multiplied < 26){//0.1 input range
                multiplied = 26;
            }
            return c.setAlpha((int) multiplied);
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid opacity, enter a value between 0 and 1!", NamedTextColor.RED)));
            return null;
        }
    }
}
