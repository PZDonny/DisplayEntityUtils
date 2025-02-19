package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

class PartsViewRangeCMD implements PlayerSubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.PARTS_VIEWRANGE)){
            return;
        }

        SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(player);
        if (partSelection == null){
            PartsCMD.noPartSelection(player);
            return;
        }
        if (!partSelection.isValid()){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Invalid part selection! Please try again!", NamedTextColor.RED)));
            return;
        }
        if (args.length < 3){
            player.sendMessage(Component.text("Provide a part tag! /mdis parts viewrange <view-range-multiplier> [-all]", NamedTextColor.RED));
            return;
        }

        try{
            float viewRange = Float.parseFloat(args[2]);
            if (args.length >= 4 && args[3].equalsIgnoreCase("-all")){
                partSelection.setViewRange(viewRange);
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("View range multiplier updated for all selected parts!", NamedTextColor.GREEN)));
            }
            else{
                partSelection.getSelectedPart().setViewRange(viewRange);
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("View range multiplier updated for your selected part!", NamedTextColor.GREEN)));
            }
            player.sendMessage(Component.text("New View Range: "+viewRange, NamedTextColor.GRAY));
        }
        catch(IllegalArgumentException e){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter a valid number!", NamedTextColor.RED)));
        }
    }
}
