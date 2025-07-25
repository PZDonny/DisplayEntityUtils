package net.donnypz.displayentityutils.command.text;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.parts.PartsCMD;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ServerSideSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;

class TextAlignCMD extends PlayerSubCommand {
    TextAlignCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("align", parentSubCommand, Permission.TEXT_SET_ALIGNMENT);
    }

    @Override
    public void execute(Player player, String[] args) {
        ServerSideSelection partSelection = DisplayGroupManager.getPartSelection(player);
        if (partSelection == null){
            DisplayEntityPluginCommand.noPartSelection(player);
            return;
        }

        if (!partSelection.hasSelectedPart()){
            PartsCMD.invalidPartSelection(player);
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
