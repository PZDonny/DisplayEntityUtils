package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.GroupResult;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class GroupMergeCMD extends PlayerSubCommand {
    GroupMergeCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("merge", parentSubCommand, Permission.GROUP_MERGE);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        if (args.length < 3) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a number for the distance from your group to attempt to merge other groups", NamedTextColor.RED)));
            player.sendMessage(Component.text("/mdis group merge <distance>", NamedTextColor.GRAY));
            return;
        }

        try{
            double radius = Double.parseDouble(args[2]);
            if (radius <= 0){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a number greater than 0 for the merging distance!", NamedTextColor.RED)));
                return;
            }
            List<GroupResult> results = DisplayGroupManager.getSpawnedGroupsNearLocation(group.getMasterPart().getEntity().getLocation(), radius);
            if (results.isEmpty() || results.size() == 1){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Your selected group is the only group within the set merging distance!", NamedTextColor.RED)));
                return;
            }
            for (GroupResult result : results){
                if (group.equals(result.group())){
                    continue;
                }
                group.merge(result.group());
            }
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Successfully merged nearby groups", NamedTextColor.GREEN)));
            group.glowAndOutline(player, 60);
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a valid number for the merging distance!", NamedTextColor.RED)));
        }
    }
}
