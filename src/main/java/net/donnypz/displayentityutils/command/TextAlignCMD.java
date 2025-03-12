package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

class TextAlignCMD extends PlayerSubCommand {
    TextAlignCMD() {
        super(Permission.TEXT_SET_ALIGNMENT);
    }

    @Override
    public void execute(Player player, String[] args) {
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
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("You can only do this with text display entities", NamedTextColor.RED)));
            return;
        }

        if (args.length < 3){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Incorrect Usage! /mdis text align <left| right | center>", NamedTextColor.RED)));
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
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Invalid Alignment!", NamedTextColor.RED)));
                player.sendMessage(Component.text("Valid alignments are \"left\", \"right\" and \"center\"", NamedTextColor.GRAY));
                return;
            }
        }
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Alignment successfully set to "+args[2], NamedTextColor.GREEN)));
    }
}
