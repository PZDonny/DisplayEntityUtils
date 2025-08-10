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
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class PartsScaleCMD extends PlayerSubCommand {

    PartsScaleCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("scale", parentSubCommand, Permission.PARTS_TRANSFORM);
    }

    @Override
    public void execute(Player player, String[] args) {
        ServerSideSelection selection = DisplayGroupManager.getPartSelection(player);
        if (selection == null){
            DisplayEntityPluginCommand.noPartSelection(player);
            return;
        }

        if (args.length < 4){
            player.sendMessage(Component.text("Incorrect Usage! /mdis parts scale <x | y | z | -all> <scale>", NamedTextColor.RED));
            return;
        }

        if (!selection.hasSelectedPart()){
            PartsCMD.invalidPartSelection(player);
            return;
        }

        try{
            String dim = args[2];
            float scale = Float.parseFloat(args[3]);
            SpawnedDisplayEntityPart part = selection.getSelectedPart();
            if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
                player.sendMessage(Component.text("You cannot do this with an interaction part entity!", NamedTextColor.RED));
                player.sendMessage(Component.text("| Use \"/mdis interaction scale\" instead", NamedTextColor.GRAY));
                return;
            }
            switch (dim.toLowerCase()){
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
                    player.sendMessage(Component.text("Incorrect Usage! /mdis parts scale <x | y | z | -all> <scale>", NamedTextColor.RED));
                    return;
                }
            }
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Scale Updated!", NamedTextColor.GREEN)));
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter a valid number for the scale!", NamedTextColor.RED)));
        }
    }
}
