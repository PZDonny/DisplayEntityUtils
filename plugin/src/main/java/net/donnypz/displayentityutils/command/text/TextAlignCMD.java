package net.donnypz.displayentityutils.command.text;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.command.parts.PartsCMD;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePartSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;

class TextAlignCMD extends PlayerSubCommand {
    TextAlignCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("align", parentSubCommand, Permission.TEXT_SET_ALIGNMENT);
    }

    @Override
    public void execute(Player player, String[] args) {
        ActivePartSelection<?> partSelection = DisplayGroupManager.getPartSelection(player);
        if (partSelection == null){
            DisplayEntityPluginCommand.noPartSelection(player);
            return;
        }

        if (!partSelection.hasSelectedPart()){
            PartsCMD.invalidPartSelection(player);
        }

        ActivePart selected = partSelection.getSelectedPart();
        if (selected.getType() != SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You can only do this with text display entities", NamedTextColor.RED)));
            return;
        }

        if (args.length < 3){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage! /mdis text align <left| right | center>", NamedTextColor.RED)));
            return;
        }

        try{
            selected.setTextDisplayAlignment(TextDisplay.TextAlignment.valueOf(args[2].toUpperCase()));
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Alignment successfully set to "+args[2], NamedTextColor.GREEN)));
        }
        catch(IllegalArgumentException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid Alignment!", NamedTextColor.RED)));
            player.sendMessage(Component.text("Valid alignments are \"left\", \"right\" and \"center\"", NamedTextColor.GRAY));
        }
    }
}
