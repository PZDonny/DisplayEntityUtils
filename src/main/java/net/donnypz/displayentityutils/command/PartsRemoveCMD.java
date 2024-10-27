package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
        if (args.length >= 3 && args[2].equalsIgnoreCase("-all")){
            for (SpawnedDisplayEntityPart part : partSelection.getSelectedParts()){
                if (part.isMaster()){
                    player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"You cannot despawn the master part! Continuing to despawn other selected parts");
                    continue;
                }
                part.remove(true);
            }
            player.sendMessage(Component.text("Successfully despawned all selected parts!", NamedTextColor.GREEN));
        }
        else{
            SpawnedDisplayEntityPart selected = partSelection.getSelectedPart();
            if (selected.isMaster()){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"You cannot despawn the master part!");
                return;
            }
            selected.remove(true);
            player.sendMessage(Component.text("Successfully despawned your selected part!", NamedTextColor.GREEN));
        }



        if (partSelection.getGroup().getSpawnedParts().size() <= 1){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.YELLOW+"Despawning your group, not enough parts remain");
            partSelection.getGroup().unregister(true, true);
            return;
        }
        partSelection.remove();


    }

}
