package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

class TextLineWidthCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.TEXT_SET_LINE_WIDTH)){
            return;
        }

        if (args.length < 3){
            player.sendMessage(Component.text("Incorrect Usage! /mdis text linewidth <width>", NamedTextColor.RED));
            return;
        }


        SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(player);
        if (partSelection == null){
            DisplayEntityPluginCommand.noPartSelection(player);
            return;
        }
        if (partSelection.getSelectedParts().size() > 1){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"You can only do this with one part selected");
            return;
        }

        if (partSelection.getSelectedParts().getFirst().getType() != SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix + ChatColor.RED + "You can only do this with text display entities");
            return;
        }
        TextDisplay display = (TextDisplay) partSelection.getSelectedParts().getFirst().getEntity();
        try{
            int change = Integer.parseInt(args[2]);
            if (change <= 0){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix+ ChatColor.RED+"Invalid width, enter a whole positive number!");
                return;
            }

            display.setLineWidth(change);
            player.sendMessage(Component.text("Successfully set text display's line width to "+change, NamedTextColor.GREEN));
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Invalid width, enter a positive whole number!");
        }
    }
}
