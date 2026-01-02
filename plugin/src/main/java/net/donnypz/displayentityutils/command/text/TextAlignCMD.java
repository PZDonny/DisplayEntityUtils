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

class TextAlignCMD extends PartsSubCommand {
    TextAlignCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("align", parentSubCommand, Permission.TEXT_SET_ALIGNMENT, 3, 3);
        setTabComplete(2, TabSuggestion.TEXT_DISPLAY_ALIGN);
    }


    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage! /deu text align <left| right | center> [-all]", NamedTextColor.RED)));
    }

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        TextDisplay.TextAlignment alignment = getAlignment(args[2], player);
        if (alignment == null) return false;
        for (ActivePart part : selection.getSelectedParts()){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
                part.setTextDisplayAlignment(alignment);
            }
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Text alignment set to "+args[2]+" for ALL selected text displays", NamedTextColor.GREEN)));
        return true;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (isInvalidType(player, selectedPart, SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY)) return false;

        TextDisplay.TextAlignment alignment = getAlignment(args[2], player);
        if (alignment == null) return false;
        selectedPart.setTextDisplayAlignment(alignment);
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Text alignment set to "+args[2], NamedTextColor.GREEN)));
        return true;
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
