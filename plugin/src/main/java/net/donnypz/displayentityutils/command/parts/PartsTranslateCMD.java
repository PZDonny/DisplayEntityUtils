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
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class PartsTranslateCMD extends PartsSubCommand {
    PartsTranslateCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("translate", parentSubCommand, Permission.PARTS_TRANSLATE, 5, 5);
        setTabComplete(2, TabSuggestion.DIRECTIONS);
        setTabComplete(3, "<distance>");
        setTabComplete(4, "<tick-duration>");
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
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("/mdis parts translate <direction> <distance> <tick-duration> [-all]", NamedTextColor.RED)));
    }

    @Override
    protected void executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        Object[] objects = getArgs(player, args);
        if (objects == null) return;

        selection.translate((Direction) objects[0], (float) objects[1], (int) objects[2], -1);
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Translating all selected parts!", NamedTextColor.GREEN)));
    }

    @Override
    protected void executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        Object[] objects = getArgs(player, args);
        if (objects == null) return;
        selectedPart.translate((Direction) objects[0], (float) objects[1], (int) objects[2], -1);
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Translating your selected part!", NamedTextColor.GREEN)));
    }

    private Object[] getArgs(Player player, String[] args){
        try{
            Direction direction = Direction.valueOf(args[2].toUpperCase());
            float distance = Float.parseFloat(args[3]);
            if (distance <= 0){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a number greater than 0 for the distance!", NamedTextColor.RED)));
                return null;
            }
            int duration = Integer.parseInt(args[4]);
            if (duration <= 0){
                duration = 0;
            }
            return new Object[]{direction, distance, duration};
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter valid numbers!", NamedTextColor.RED)));
            player.sendMessage(Component.text("Duration must be a positive whole number, distance can be any positive number", NamedTextColor.GRAY, TextDecoration.ITALIC));
        }
        catch(IllegalArgumentException e){
            DisplayEntityPluginCommand.invalidDirection(player);
        }
        return null;
    }
}
