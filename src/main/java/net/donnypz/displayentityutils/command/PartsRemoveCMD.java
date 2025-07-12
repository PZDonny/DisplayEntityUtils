package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

class PartsRemoveCMD extends PlayerSubCommand {
    PartsRemoveCMD() {
        super(Permission.PARTS_REMOVE);
    }

    @Override
    public void execute(Player player, String[] args) {
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
                    continue;
                }
                part.remove(true);
            }
            player.sendMessage(Component.text("Successfully despawned all selected parts!", NamedTextColor.GREEN));
        }
        else{
            SpawnedDisplayEntityPart selected = partSelection.getSelectedPart();
            if (selected.isMaster()){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("You cannot despawn the master/parent part!", NamedTextColor.RED)));
                return;
            }
            selected.remove(true);
            player.sendMessage(Component.text("Successfully despawned your selected part!", NamedTextColor.GREEN));
        }

        if (partSelection.getSize() == 0){
            partSelection.remove();
            player.sendMessage(Component.text("Part selection reset! (No parts remaining)", NamedTextColor.RED));
        }

        if (group.getParts().size() <= 1){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Despawning your group, not enough parts remain", NamedTextColor.YELLOW)));
            DEUCommandUtils.removeRelativePoints(player);
            group.unregister(true, true);
        }
    }
}
