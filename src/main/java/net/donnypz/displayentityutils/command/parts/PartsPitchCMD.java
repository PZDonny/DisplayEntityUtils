package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ServerSideSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;

class PartsPitchCMD extends PlayerSubCommand {

    PartsPitchCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("pitch", parentSubCommand, Permission.PARTS_TRANSFORM);
    }

    @Override
    public void execute(Player player, String[] args) {
        ServerSideSelection selection = DisplayGroupManager.getPartSelection(player);
        if (selection == null){
            DisplayEntityPluginCommand.noPartSelection(player);
            return;
        }

        if (PartsCMD.isUnwantedMultiSelection(player, selection)){
            return;
        }

        if (args.length < 3){
            player.sendMessage(Component.text("Incorrect Usage! /mdis parts pitch <pitch>", NamedTextColor.RED));
            return;
        }

        if (!selection.hasSelectedPart()){
            PartsCMD.invalidPartSelection(player);
            return;
        }

        try{
            float pitch = Float.parseFloat(args[2]);
            SpawnedDisplayEntityPart part = selection.getSelectedPart();
            double oldPitch = part.getPitch();
            selection.getSelectedPart().setPitch(pitch);
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Pitch set!", NamedTextColor.GREEN)));
            player.sendMessage(Component.text("| Old Pitch: "+oldPitch, NamedTextColor.GRAY));
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter a valid number for the pitch!", NamedTextColor.RED)));
        }
    }
}
