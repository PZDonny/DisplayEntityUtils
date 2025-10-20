package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class GroupScaleCMD extends GroupSubCommand {
    GroupScaleCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("scale", parentSubCommand, Permission.GROUP_TRANSFORM, 4, true);
        setTabComplete(2, "<scale-multiplier>");
        setTabComplete(3, "<tick-duration>");
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(Component.text("/mdis group scale <scale-multiplier> <tick-duration>", NamedTextColor.RED));
    }

    @Override
    protected void execute(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull String[] args) {
        if (RelativePointUtils.isViewingRelativePoints(player)){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You cannot play do that while viewing points!", NamedTextColor.RED)));
            return;
        }
        try{
            float multiplier = Float.parseFloat(args[2]);
            if (multiplier <= 0){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a number greater than 0 for the scale multiplier!", NamedTextColor.RED)));
                return;
            }
            int duration = Integer.parseInt(args[3]);
            if (duration < 0){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a whole number, 0 or greater, for the duration!", NamedTextColor.RED)));
                return;
            }
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Scaling your selected group!", NamedTextColor.GREEN)));
            player.sendMessage(Component.text("Old Scale: "+group.getScaleMultiplier()+"x", NamedTextColor.GRAY));
            player.sendMessage(Component.text("New Scale: "+multiplier+"x", NamedTextColor.YELLOW));
            group.scale(multiplier, duration, true);
        }
        catch(IllegalArgumentException e){
            if (e instanceof NumberFormatException){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter valid numbers!", NamedTextColor.RED)));
            }
        }
    }
}
