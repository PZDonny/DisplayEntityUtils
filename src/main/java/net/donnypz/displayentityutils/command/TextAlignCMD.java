package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

class TextAlignCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.TEXT_SET_ALIGNMENT)){
            return;
        }

        SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(player);
        if (partSelection == null){
            DisplayEntityPluginCommand.noPartSelection(player);
            return;
        }

        if (partSelection.getSelectedParts().isEmpty()){
            PartsCMD.invalidPartSelection(player);
            return;
        }

        SpawnedDisplayEntityPart selected = partSelection.getSelectedPart();
        if (selected.getType() != SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix + ChatColor.RED + "You can only do this with text display entities");
            return;
        }

        if (args.length < 3){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix+ ChatColor.RED+"Incorrect Usage! /mdis text align <left| right | center>");
            return;
        }

        TextDisplay display = (TextDisplay) selected.getEntity();
        String font = args[2];
        switch(font){
            case "left" -> {
                display.setAlignment(TextDisplay.TextAlignment.LEFT);
            }
            case "right" -> {
                display.setAlignment(TextDisplay.TextAlignment.RIGHT);
            }
            case "center" -> {
                display.setAlignment(TextDisplay.TextAlignment.CENTER);
            }
            default -> {
                player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Invalid Alignment!");
                player.sendMessage(ChatColor.GRAY+"Valid alignments are \"left\", \"right\" and \"center\"");
                return;
            }
        }
        player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Alignment successfully set to "+args[2]);
    }
}
