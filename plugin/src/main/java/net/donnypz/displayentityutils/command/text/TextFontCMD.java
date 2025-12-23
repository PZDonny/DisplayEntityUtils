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
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage! /deu text font <default | alt | uniform | illageralt> [-all]", NamedTextColor.RED)));
    }

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        Key font = getFont(args, player);
        if (font == null) return false;
        for (ActivePart part : selection.getSelectedParts()){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
                part.setTextDisplayText(part.getTextDisplayText().font(font));
            }
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Font set to "+args[2]+" for ALL selected text displays", NamedTextColor.GREEN)));
        return true;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (isInvalidType(player, selectedPart, SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY)) return false;

        Key font = getFont(args, player);
        if (font == null) return false;
        selectedPart.setTextDisplayText(selectedPart.getTextDisplayText().font(font));
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Font set to "+args[2], NamedTextColor.GREEN)));
        return true;
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
