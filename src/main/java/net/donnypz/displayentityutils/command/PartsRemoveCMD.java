package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

class PartsRemoveCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.PARTS_REMOVE)){
            return;
        }

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(player);
        if (partSelection == null){
            PartsCMD.noPartSelection(player);
            return;
        }

        for (SpawnedDisplayEntityPart part : partSelection.getSelectedParts()){
            if (part.isMaster()){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"You cannot despawn the master part! Continuing to despawn other selected parts");
                continue;
            }
            part.remove(true);
        }

        if (partSelection.getGroup().getSpawnedParts().size() <= 1){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.YELLOW+"Despawning your group, not enough parts remain");
            partSelection.getGroup().unregister(true);
            return;
        }
        partSelection.remove();


    }

}
