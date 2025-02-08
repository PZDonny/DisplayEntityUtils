package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

class TextFontCMD implements PlayerSubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.TEXT_SET_FONT)){
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
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("You can only do this with text display entities", NamedTextColor.RED)));
            return;
        }

        if (args.length < 3){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Incorrect Usage! /mdis text font <default | alt | uniform | illageralt>", NamedTextColor.RED)));
            return;
        }

        TextDisplay display = (TextDisplay) selected.getEntity();
        String font = args[2];
        switch(font){
            case "default", "alt", "uniform", "illageralt" -> {
                display.text(display.text().font(Key.key("minecraft:"+font)));
            }
            default -> {
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Invalid Font!", NamedTextColor.RED)));
                player.sendMessage(Component.text("Valid fonts are \"default\", \"alt\", \"uniform\" and \"illageralt\"", NamedTextColor.GRAY));
                return;
            }
        }
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Font successfully set to "+args[2], NamedTextColor.GREEN)));
    }
}
