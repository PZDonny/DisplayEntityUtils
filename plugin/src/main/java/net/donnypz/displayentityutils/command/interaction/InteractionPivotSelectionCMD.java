package net.donnypz.displayentityutils.command.interaction;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.command.parts.PartsCMD;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ServerSideSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class InteractionPivotSelectionCMD extends PlayerSubCommand {
    InteractionPivotSelectionCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("pivotselection", parentSubCommand, Permission.INTERACTION_PIVOT);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null){
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        if (args.length < 3){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage! /mdis interaction pivotselection <angle>", NamedTextColor.RED)));
            return;
        }

        try{
            float angle = Float.parseFloat(args[2]);


            ServerSideSelection sel = DisplayGroupManager.getPartSelection(player);

            if (PartsCMD.isUnwantedSingleSelection(player, sel)){
                return;
            }

            if (sel != null){
                ((SpawnedPartSelection) sel).pivot(angle);
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Pivoting all Interaction Entities in part selection around group!", NamedTextColor.GREEN)));
            }

            else{
                for (SpawnedDisplayEntityPart part : group.getParts(SpawnedDisplayEntityPart.PartType.INTERACTION)){
                    part.pivot(angle);
                }
            }
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a valid number for the angle!", NamedTextColor.RED)));
        }
    }
}
