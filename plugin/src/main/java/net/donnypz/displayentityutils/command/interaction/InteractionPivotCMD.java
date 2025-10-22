package net.donnypz.displayentityutils.command.interaction;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.command.parts.PartsCMD;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class InteractionPivotCMD extends PlayerSubCommand {
    InteractionPivotCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("pivot", parentSubCommand, Permission.INTERACTION_PIVOT);
        setTabComplete(2, "<angle>");
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

        MultiPartSelection<?> selection = (MultiPartSelection<?>) sel;
        if (args.length < 3){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage! /mdis interaction pivot <angle>", NamedTextColor.RED)));
            return;
        }
        InteractionCMD.SelectedInteraction interaction = InteractionCMD.getInteraction(player, false);
        if (interaction == null){
            return;
        }

        try{
            interaction.pivot(selection.getGroup().getLocation(), Double.parseDouble(args[2]));
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Pivoting Interaction around group", NamedTextColor.GREEN)));
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a valid number for the angle!", NamedTextColor.RED)));
        }
    }
}
