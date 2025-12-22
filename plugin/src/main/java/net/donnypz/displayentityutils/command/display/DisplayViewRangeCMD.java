package net.donnypz.displayentityutils.command.display;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class DisplayViewRangeCMD extends PartsSubCommand {
    DisplayViewRangeCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("viewrange", parentSubCommand, Permission.DISPLAY_VIEW_RANGE, 3, 3);
        setTabComplete(2, "<view-range-multiplier>");
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(Component.text("Provide a part tag! /deu display viewrange <view-range-multiplier> [-all]", NamedTextColor.RED));
    }

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        Float viewRange = getViewRange(player, args[2]);
        if (viewRange == null) return false;
        selection.setViewRange(viewRange);
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("View range multiplier updated for all selected displays!", NamedTextColor.GREEN)));
        player.sendMessage(Component.text("New View Range: "+viewRange, NamedTextColor.GRAY));
        return true;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        Float viewRange = getViewRange(player, args[2]);
        if (viewRange == null) return false;
        selectedPart.setViewRange(viewRange);
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("View range multiplier updated for your selected part!", NamedTextColor.GREEN)));
        player.sendMessage(Component.text("New View Range: "+viewRange, NamedTextColor.GRAY));
        return true;
    }

    private Float getViewRange(Player player, String arg){
        try{
            return Float.parseFloat(arg);
        }
        catch(IllegalArgumentException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a valid number!", NamedTextColor.RED)));
        }
        return null;
    }
}
