package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

class GroupCloneCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.GROUP_CLONE)){
            return;
        }

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        clone(player, group.clone(group.getMasterPart().getEntity().getLocation()));
    }

    static void clone(Player p, SpawnedDisplayEntityGroup clonedGroup){
        if (clonedGroup == null){
            p.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Failed to clone spawned display entity group!", NamedTextColor.RED)));
        }
        else{
            p.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Successfully cloned your selected group", NamedTextColor.GREEN)));
            p.sendMessage(Component.text("- Your group selection has been changes to the newly created group", NamedTextColor.GRAY, TextDecoration.ITALIC));
            DisplayGroupManager.setSelectedSpawnedGroup(p, clonedGroup);
            clonedGroup.glow(80, false);
        }
    }
}
