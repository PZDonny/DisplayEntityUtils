package net.donnypz.displayentityutils.command.interaction;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.command.parts.PartsCMD;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class InteractionPivotCMD extends PlayerSubCommand {
    InteractionPivotCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("pivot", parentSubCommand, Permission.INTERACTION_PIVOT);
        setTabComplete(2, "<angle>");
        setTabComplete(3, "-all");
    }

    @Override
    public void execute(Player player, String[] args) {
        ActiveGroup<?> group = DisplayGroupManager.getSelectedGroup(player);
        if (group == null){
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        ActivePartSelection<?> sel = DisplayGroupManager.getPartSelection(player);
        if (sel == null){
            DisplayEntityPluginCommand.noPartSelection(player);
            return;
        }

        if (PartsCMD.isUnwantedSingleSelection(player, sel)){
            return;
        }

        if (args.length < 3){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage! /deu interaction pivot <angle> [-all]", NamedTextColor.RED)));
            return;
        }

        double angle;
        try{
            angle = Double.parseDouble(args[2]);;
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a valid number for the angle!", NamedTextColor.RED)));
            return;
        }


        MultiPartSelection<?> selection = (MultiPartSelection<?>) sel;
        boolean isAll = args.length >= 4 && args[3].equalsIgnoreCase("-all");
        if (isAll){
            for (ActivePart p : selection.getSelectedParts()){
                if (p.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
                    p.pivot((float) angle);
                }
            }
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Pivoting ALL Interaction entities in your selection around your group", NamedTextColor.GREEN)));
        }
        else{
            InteractionCMD.SelectedInteraction interaction = InteractionCMD.getInteraction(player, false);
            if (interaction == null){
                return;
            }
            interaction.pivot(selection.getGroup().getLocation(), angle);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Pivoting Interaction around group", NamedTextColor.GREEN)));
        }
    }
}
