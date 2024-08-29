package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.GroupResult;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

class GroupCopyPoseCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.GROUP_COPY_POSE)){
            return;
        }

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        GroupResult copy = DisplayGroupManager.getSpawnedGroupNearLocation(player.getLocation(), 1, player);
        if (copy == null || copy.group() == group){
            player.sendMessage(Component.text("Failed to find a spawned display entity group to copy to your selected display entity group!", NamedTextColor.RED));
            return;
        }
        group.copyTransformation(copy.group());
        copy.group().unregister(true);
    }
}
