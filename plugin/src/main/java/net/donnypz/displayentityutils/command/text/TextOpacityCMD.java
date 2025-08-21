package net.donnypz.displayentityutils.command.text;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.command.parts.PartsCMD;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ServerSideSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;

class TextOpacityCMD extends PlayerSubCommand {
    TextOpacityCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("opacity", parentSubCommand, Permission.TEXT_OPACITY);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 3){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage! /mdis text opacity <0-1>", NamedTextColor.RED)));
            return;
        }


        ServerSideSelection partSelection = DisplayGroupManager.getPartSelection(player);
        if (partSelection == null){
            DisplayEntityPluginCommand.noPartSelection(player);
            return;
        }
        if (!partSelection.hasSelectedPart()){
            PartsCMD.invalidPartSelection(player);
        }

        SpawnedDisplayEntityPart selected = partSelection.getSelectedPart();
        if (selected.getType() != SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You can only do this with text display entities", NamedTextColor.RED)));
            return;
        }
        TextDisplay display = (TextDisplay) selected.getEntity();
        try{
            double change = Double.parseDouble(args[2]);
            if (change < 0 || change > 1){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid opacity, enter a value between 0 and 1!", NamedTextColor.RED)));
                return;
            }
            double percentage = 255*change;
            if (percentage >= 25.5 && percentage < 26){//0.1 input range
                percentage = 26;
            }
            byte opacity = (byte) percentage;
            if (opacity > -1 && opacity < 26){ //Adjusted for Minecraft Shader Values (Outside of 0.1 range)
                opacity = 25;
            }

            display.setTextOpacity(opacity);
            player.sendMessage(Component.text("Successfully set text display's opacity to "+change, NamedTextColor.GREEN));
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid opacity, enter a value between 0 and 1!", NamedTextColor.RED)));
        }
    }
}
