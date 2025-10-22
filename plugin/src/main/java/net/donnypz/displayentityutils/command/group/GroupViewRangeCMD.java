package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class GroupViewRangeCMD extends GroupSubCommand {
    GroupViewRangeCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("viewrange", parentSubCommand, Permission.GROUP_VIEWRANGE, 3, true);
        setTabComplete(2, "<view-range-multiplier>");
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage! /mdis group viewrange <view-range-multiplier>", NamedTextColor.RED)));
    }

    @Override
    protected void execute(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull String[] args) {
        try{
            float viewRange = Float.parseFloat(args[2]);
            group.setViewRange(viewRange);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Group view range multiplier updated!", NamedTextColor.GREEN)));
            player.sendMessage(Component.text("View Range: "+viewRange, NamedTextColor.GRAY));
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a valid number!", NamedTextColor.RED)));
        }
    }
}
