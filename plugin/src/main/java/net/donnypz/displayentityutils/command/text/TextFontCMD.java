package net.donnypz.displayentityutils.command.text;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class TextFontCMD extends PartsSubCommand {

    public TextFontCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("font", parentSubCommand, Permission.TEXT_SET_FONT, 3, 3);
        setTabComplete(2, TabSuggestion.TEXT_DISPLAY_FONTS);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage! /mdis text font <default | alt | uniform | illageralt> [-all]", NamedTextColor.RED)));
    }

    @Override
    protected void executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        Key font = getFont(args, player);
        if (font == null) return;
        for (ActivePart part : selection.getSelectedParts()){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
                part.setTextDisplayText(part.getTextDisplayText().font(font));
            }
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Font successfully set to "+args[2]+" for ALL selected text displays", NamedTextColor.GREEN)));
    }

    @Override
    protected void executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (selectedPart.getType() != SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You can only do this with text display entities", NamedTextColor.RED)));
            return;
        }
        Key font = getFont(args, player);
        if (font == null) return;
        selectedPart.setTextDisplayText(selectedPart.getTextDisplayText().font(font));
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Font successfully set to "+args[2], NamedTextColor.GREEN)));
    }

    private Key getFont(String[] args, Player player){
        String font = args[2];
        switch(font){
            case "default", "alt", "uniform", "illageralt" -> {
                return Key.key("minecraft:"+font);
            }
            default -> {
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid Font!", NamedTextColor.RED)));
                player.sendMessage(Component.text("Valid fonts are \"default\", \"alt\", \"uniform\" and \"illageralt\"", NamedTextColor.GRAY));
                return null;
            }
        }
    }
}
