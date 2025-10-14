package net.donnypz.displayentityutils.command.text;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.command.parts.PartsCMD;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePartSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class TextFontCMD extends PlayerSubCommand {

    public TextFontCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("font", parentSubCommand, Permission.TEXT_SET_FONT);
    }

    @Override
    public void execute(Player player, String[] args) {
        ActivePartSelection<?> partSelection = DisplayGroupManager.getPartSelection(player);
        if (partSelection == null){
            DisplayEntityPluginCommand.noPartSelection(player);
            return;
        }

        if (!partSelection.hasSelectedPart()){
            PartsCMD.invalidPartSelection(player);
        }

        ActivePart selected = partSelection.getSelectedPart();
        if (selected.getType() != SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You can only do this with text display entities", NamedTextColor.RED)));
            return;
        }

        if (args.length < 3){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage! /mdis text font <default | alt | uniform | illageralt>", NamedTextColor.RED)));
            return;
        }

        String font = args[2];
        switch(font){
            case "default", "alt", "uniform", "illageralt" -> {
                Component text = selected.getTextDisplayText();
                selected.setTextDisplayText(text.font(Key.key("minecraft:"+font)));
            }
            default -> {
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid Font!", NamedTextColor.RED)));
                player.sendMessage(Component.text("Valid fonts are \"default\", \"alt\", \"uniform\" and \"illageralt\"", NamedTextColor.GRAY));
                return;
            }
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Font successfully set to "+args[2], NamedTextColor.GREEN)));
    }
}
