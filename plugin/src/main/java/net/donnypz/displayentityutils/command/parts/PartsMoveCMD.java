package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class PartsMoveCMD extends PartsSubCommand {

    public PartsMoveCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("move", parentSubCommand, Permission.PARTS_TRANSFORM, 4, 4);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (RelativePointUtils.isViewingRelativePoints(player)){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You cannot play do that while viewing points!", NamedTextColor.RED)));
            return;
        }
        super.execute(player, args);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(Component.text("Incorrect Usage! /deu parts move <direction> <distance>", NamedTextColor.RED));
    }

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        try{
            Direction direction = Direction.valueOf(args[2].toUpperCase());
            float distance = Float.parseFloat(args[3]);
            if (distance <= 0){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a number greater than 0 for the distance!", NamedTextColor.RED)));
                return false;
            }
            for (ActivePart part : selection.getSelectedParts()){
                if (part.isDisplay()) continue;
                Location loc = part.getLocation();
                Vector v = direction.getVector(selection.getSelectedPart(), false).normalize().multiply(distance);
                loc.add(v);
                part.teleport(loc);
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Moved ALL selected non-display parts!", NamedTextColor.GREEN)));
            }
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter valid numbers!", NamedTextColor.RED)));
            player.sendMessage(Component.text("Duration must be a positive whole number, distance can be any positive number", NamedTextColor.GRAY, TextDecoration.ITALIC));
            return false;
        }
        catch(IllegalArgumentException e){
            DisplayEntityPluginCommand.invalidDirection(player);
            return false;
        }
        return true;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (selectedPart.isDisplay() && group != null){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You cannot do this for grouped display entities!", NamedTextColor.RED)));
            player.sendMessage(Component.text("| Use \"/deu display translate\" instead", NamedTextColor.GRAY));
            return false;
        }
        try{
            Direction direction = Direction.valueOf(args[2].toUpperCase());
            float distance = Float.parseFloat(args[3]);
            if (distance <= 0){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a number greater than 0 for the distance!", NamedTextColor.RED)));
                return false;
            }
            Location loc = selectedPart.getLocation();
            Vector v = direction.getVector(selection.getSelectedPart(), false).normalize().multiply(distance);
            loc.add(v);
            selectedPart.teleport(loc);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Moved your selected part!", NamedTextColor.GREEN)));
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter valid numbers!", NamedTextColor.RED)));
            player.sendMessage(Component.text("Duration must be a positive whole number, distance can be any positive number", NamedTextColor.GRAY, TextDecoration.ITALIC));
            return false;
        }
        catch(IllegalArgumentException e){
            DisplayEntityPluginCommand.invalidDirection(player);
            return false;
        }
        return true;
    }
}
