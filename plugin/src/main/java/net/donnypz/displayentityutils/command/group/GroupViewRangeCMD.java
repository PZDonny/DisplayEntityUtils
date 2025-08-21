package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class GroupViewRangeCMD extends PlayerSubCommand {
    GroupViewRangeCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("viewrange", parentSubCommand, Permission.GROUP_VIEWRANGE);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        if (args.length < 3) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage! /mdis group viewrange <view-range-multiplier>", NamedTextColor.RED)));
            return;
        }

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
