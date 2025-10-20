package net.donnypz.displayentityutils.command.text;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

class TextAlignCMD extends PartsSubCommand {
    TextAlignCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("align", parentSubCommand, Permission.TEXT_SET_ALIGNMENT, 3, 3);
        setTabComplete(2, TabSuggestion.TEXT_DISPLAY_ALIGN);
    }


    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage! /mdis text align <left| right | center> [-all]", NamedTextColor.RED)));
    }

    @Override
    protected void executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        TextDisplay.TextAlignment alignment = getAlignment(args[2], player);
        if (alignment == null) return;
        for (ActivePart part : selection.getSelectedParts()){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
                part.setTextDisplayAlignment(alignment);
            }
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Text alignment successfully set to "+args[2]+" for ALL selected text displays", NamedTextColor.GREEN)));
    }

    @Override
    protected void executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (selectedPart.getType() != SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You can only do this with text display entities", NamedTextColor.RED)));
            return;
        }
        TextDisplay.TextAlignment alignment = getAlignment(args[2], player);
        if (alignment == null) return;
        selectedPart.setTextDisplayAlignment(alignment);
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Text alignment successfully set to "+args[2], NamedTextColor.GREEN)));
    }

    private TextDisplay.TextAlignment getAlignment(String alignment, Player player){
        try{
            return TextDisplay.TextAlignment.valueOf(alignment.toUpperCase());
        }
        catch(IllegalArgumentException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid Alignment!", NamedTextColor.RED)));
            player.sendMessage(Component.text("Valid alignments are \"left\", \"right\" and \"center\"", NamedTextColor.GRAY));
            return null;
        }
    }
}
