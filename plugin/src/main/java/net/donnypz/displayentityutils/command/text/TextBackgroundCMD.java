package net.donnypz.displayentityutils.command.text;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.command.parts.PartsCMD;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.ConversionUtils;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePartSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;

class TextBackgroundCMD extends PlayerSubCommand {
    TextBackgroundCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("background", parentSubCommand, Permission.TEXT_BACKGROUND);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 4){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage! /mdis text background <color | hex-code> <0-1>", NamedTextColor.RED)));
            return;
        }


        ActivePartSelection<?> partSelection = DisplayGroupManager.getPartSelection(player);
        if (partSelection == null){
            DisplayEntityPluginCommand.noPartSelection(player);
            return;
        }

        if (!partSelection.hasSelectedPart()){
            PartsCMD.invalidPartSelection(player);
        }

        Color c = ConversionUtils.getColorFromText(args[2]);
        if (c == null){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a valid color!", NamedTextColor.RED)));
            player.sendMessage(Component.text("/mdis text background <color | hex-code> <0-1>", NamedTextColor.GRAY));
            return;
        }

        ActivePart selected = partSelection.getSelectedPart();
        if (selected.getType() != SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You can only do this with text display entities", NamedTextColor.RED)));
            return;
        }

        try{
            double change = Double.parseDouble(args[3]);
            if (change < 0 || change > 1){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid opacity, enter a value between 0 and 1!", NamedTextColor.RED)));
                return;
            }
            double multiplied = 255*change;
            if (multiplied >= 25.5 && multiplied < 26){//0.1 input range
                multiplied = 26;
            }

            selected.setTextDisplayBackgroundColor(c.setAlpha((int) multiplied));
            player.sendMessage(Component.text("Successfully set text display's background color", NamedTextColor.GREEN));
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid opacity, enter a value between 0 and 1!", NamedTextColor.RED)));
        }
    }
}
