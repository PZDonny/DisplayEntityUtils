package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

class InteractionPivotSelectionCMD extends PlayerSubCommand {
    InteractionPivotSelectionCMD() {
        super(Permission.INTERACTION_PIVOT);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null){
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        if (args.length < 3){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Incorrect Usage! /mdis interaction pivotselection <angle>", NamedTextColor.RED)));
            return;
        }

        try{
            double angle = Double.parseDouble(args[2]);

            SpawnedPartSelection selection = DisplayGroupManager.getPartSelection(player);
            if (selection != null){
                selection.pivot(angle);
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Pivoting all Interaction Entities in part selection around group!", NamedTextColor.GREEN)));
            }

            else{
                for (SpawnedDisplayEntityPart part : group.getSpawnedParts(SpawnedDisplayEntityPart.PartType.INTERACTION)){
                    part.pivot(angle);
                }
            }
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter a valid number for the angle!", NamedTextColor.RED)));
        }
    }
}
