package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class GroupMoveCMD extends GroupSubCommand {
    GroupMoveCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("move", parentSubCommand, Permission.GROUP_TRANSFORM, 4, false);
        setTabComplete(2, TabSuggestion.DIRECTIONS);
        setTabComplete(3, "<distance>");
        setTabComplete(4, "[tick-duration]");
    }


    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(Component.text("/deu group move <direction> <distance> [tick-duration]", NamedTextColor.RED));
    }

    @Override
    protected void execute(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull String[] args) {
        if (RelativePointUtils.isViewingRelativePoints(player)){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You cannot play do that while viewing points!", NamedTextColor.RED)));
            return;
        }

        try{
            Direction direction = Direction.valueOf(args[2].toUpperCase());
            double distance = Double.parseDouble(args[3]);
            if (distance <= 0){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a number greater than 0 for the distance!", NamedTextColor.RED)));
                return;
            }

            if (args.length == 4){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Teleporting your selected group!", NamedTextColor.GREEN)));
                group.teleport(direction, distance);
                return;
            }

            //With duration
            int duration = Integer.parseInt(args[4]);
            if (duration <= 0){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a whole number greater than 0 for the duration!", NamedTextColor.RED)));
                return;
            }
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Moving your selected group!", NamedTextColor.GREEN)));
            group.teleportMove(direction, distance, duration);
        }
        catch(IllegalArgumentException e){
            if (e instanceof NumberFormatException){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter valid numbers!", NamedTextColor.RED)));
            }
            else{
                DisplayEntityPluginCommand.invalidDirection(player);
            }
        }
    }
}
