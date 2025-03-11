package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;

class InteractionPivotCMD extends PlayerSubCommand {
    InteractionPivotCMD() {
        super(Permission.INTERACTION_PIVOT);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null){
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }
        SpawnedPartSelection selection = DisplayGroupManager.getPartSelection(player);
        if (selection == null){
            DisplayEntityPluginCommand.noPartSelection(player);
            return;
        }
        if (args.length < 3){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Incorrect Usage! /mdis interaction pivot <angle>", NamedTextColor.RED)));
            return;
        }
        Interaction interaction = InteractionCMD.getInteraction(player, false);
        if (interaction == null){
            return;
        }
        try{
            DisplayUtils.pivot(interaction, selection.getGroup().getLocation(), Double.parseDouble(args[2]));
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Pivoting Interaction Entity around group", NamedTextColor.GREEN)));
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter a valid number for the angle!", NamedTextColor.RED)));
            return;
        }
    }
}
