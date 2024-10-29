package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

class PartsAdaptTagsCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.PARTS_TAG)){
            return;
        }

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null){
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        boolean removeFromSB;
        if (args.length < 2){
            removeFromSB = false;
        }
        else removeFromSB = args[1].equals("-remove");

        SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(player);
        if (partSelection == null){ //Adapt for all parts
            for (SpawnedDisplayEntityPart part : group.getSpawnedParts()){
                part.adaptScoreboardTags(removeFromSB);
            }
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Adapted all scoreboard tags in your selected group!", NamedTextColor.GREEN)));
        }
        else{ //Adapt for selection
            if (!partSelection.isValid()){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Invalid part selection! Please try again!", NamedTextColor.RED)));
                return;
            }
            for (SpawnedDisplayEntityPart part : partSelection.getSelectedParts()){
                part.adaptScoreboardTags(removeFromSB);
            }
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Adapted all scoreboard tags in your part selection!", NamedTextColor.GREEN)));
        }
    }

}
