package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.GroupResult;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

class GroupMergeCMD extends GroupSubCommand {
    GroupMergeCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("merge", parentSubCommand, Permission.GROUP_MERGE, 3, false);
        setTabComplete(2, "<distance>");
    }

    @Override
    public void execute(Player player, String[] args) {

    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a number for the distance from your group to attempt to merge other groups", NamedTextColor.RED)));
        player.sendMessage(Component.text("/deu group merge <distance>", NamedTextColor.GRAY));
    }

    @Override
    protected void execute(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull String[] args) {
        if (group instanceof PacketDisplayEntityGroup){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Packet-based groups cannot be merged!", NamedTextColor.RED)));
            return;
        }

        SpawnedDisplayEntityGroup sg = (SpawnedDisplayEntityGroup) group;
        try{
            double radius = Double.parseDouble(args[2]);
            if (radius <= 0){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a number greater than 0 for the merging distance!", NamedTextColor.RED)));
                return;
            }
            List<GroupResult> results = DisplayGroupManager.getSpawnedGroupsNearLocation(sg.getLocation(), radius);
            if (results.isEmpty() || results.size() == 1){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Your selected group is the only group within the set merging distance!", NamedTextColor.RED)));
                return;
            }
            for (GroupResult result : results){
                if (group.equals(result.group())){
                    continue;
                }
                sg.merge(result.group());
            }
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Successfully merged nearby groups", NamedTextColor.GREEN)));
            sg.glowAndMarkInteractions(player, 60);
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a valid number for the merging distance!", NamedTextColor.RED)));
        }
    }
}
