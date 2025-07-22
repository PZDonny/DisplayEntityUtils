package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ServerSideSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class PartsViewRangeCMD extends PartsSubCommand {
    PartsViewRangeCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("viewrange", parentSubCommand, Permission.PARTS_VIEWRANGE, 3, 3);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(Component.text("Provide a part tag! /mdis parts viewrange <view-range-multiplier> [-all]", NamedTextColor.RED));
    }

    @Override
    protected void executeAllPartsAction(@NotNull Player player, @Nullable SpawnedDisplayEntityGroup group, @NotNull SpawnedPartSelection selection, @NotNull String[] args) {
        Float viewRange = getViewRange(player, args[2]);
        if (viewRange == null) return;
        selection.setViewRange(viewRange);
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("View range multiplier updated for all selected parts!", NamedTextColor.GREEN)));
        player.sendMessage(Component.text("New View Range: "+viewRange, NamedTextColor.GRAY));
    }

    @Override
    protected void executeSinglePartAction(@NotNull Player player, @Nullable SpawnedDisplayEntityGroup group, @NotNull ServerSideSelection selection, @NotNull SpawnedDisplayEntityPart selectedPart, @NotNull String[] args) {
        Float viewRange = getViewRange(player, args[2]);
        if (viewRange == null) return;
        selectedPart.setViewRange(viewRange);
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("View range multiplier updated for your selected part!", NamedTextColor.GREEN)));
        player.sendMessage(Component.text("New View Range: "+viewRange, NamedTextColor.GRAY));
    }

    private Float getViewRange(Player player, String arg){
        try{
            return Float.parseFloat(arg);
        }
        catch(IllegalArgumentException e){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter a valid number!", NamedTextColor.RED)));
        }
        return null;
    }
}
