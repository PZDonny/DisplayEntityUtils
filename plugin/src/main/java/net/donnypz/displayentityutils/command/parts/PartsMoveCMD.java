package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePartSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

class PartsMoveCMD extends PlayerSubCommand {

    PartsMoveCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("move", parentSubCommand, Permission.PARTS_TRANSFORM);
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
            player.sendMessage(Component.text("Incorrect Usage! /mdis parts move <direction> <distance>", NamedTextColor.RED));
            return;
        }

        if (!selection.hasSelectedPart()){
            PartsCMD.invalidPartSelection(player);
            return;
        }

        try{
            Direction direction = Direction.valueOf(args[2].toUpperCase());
            float distance = Float.parseFloat(args[3]);
            if (distance <= 0){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a number greater than 0 for the distance!", NamedTextColor.RED)));
                return;
            }
            SpawnedDisplayEntityPart part = (SpawnedDisplayEntityPart) selection.getSelectedPart();
            Location loc = part.getLocation();
            Vector v = direction.getVector(selection.getSelectedPart(), false).normalize().multiply(distance);
            loc.add(v);
            part.getEntity().teleport(loc);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Moved your selected part!", NamedTextColor.GREEN)));
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter valid numbers!", NamedTextColor.RED)));
            player.sendMessage(Component.text("Duration must be a positive whole number, distance can be any positive number", NamedTextColor.GRAY, TextDecoration.ITALIC));
        }
        catch(IllegalArgumentException e){
            DisplayEntityPluginCommand.invalidDirection(player);
        }
    }
}
