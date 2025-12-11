package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class PartsPitchCMD extends PlayerSubCommand {

    PartsPitchCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("pitch", parentSubCommand, Permission.PARTS_TRANSFORM);
        setTabComplete(2, "<pitch>");
    }

    @Override
    public void execute(Player player, String[] args) {
        ActivePartSelection<?> selection = DisplayGroupManager.getPartSelection(player);
        if (selection == null){
            DisplayEntityPluginCommand.noPartSelection(player);
            return;
        }

        if (PartsCMD.isUnwantedMultiSelection(player, selection)){
            return;
        }

        if (args.length < 3){
            player.sendMessage(Component.text("Incorrect Usage! /deu parts pitch <pitch>", NamedTextColor.RED));
            return;
        }

        if (!selection.hasSelectedPart()){
            PartsCMD.invalidPartSelection(player);
            return;
        }

        try{
            float pitch = Float.parseFloat(args[2]);
            ActivePart part = selection.getSelectedPart();
            double oldPitch = part.getPitch();
            part.setPitch(pitch);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Pitch set!", NamedTextColor.GREEN)));
            player.sendMessage(Component.text("| Old Pitch: "+oldPitch, NamedTextColor.GRAY));
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a valid number for the pitch!", NamedTextColor.RED)));
        }
    }
}
