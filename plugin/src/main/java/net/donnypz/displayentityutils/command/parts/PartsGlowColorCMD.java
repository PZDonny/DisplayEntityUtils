package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.ConversionUtils;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class PartsGlowColorCMD extends PartsSubCommand {
    PartsGlowColorCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("glowcolor", parentSubCommand, Permission.PARTS_GLOW_COLOR, 3, 3);
        setTabComplete(2, TabSuggestion.COLORS);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a valid color!", NamedTextColor.RED)));
        player.sendMessage(Component.text("/deu parts glowcolor <color | hex-code> [-all]", NamedTextColor.GRAY));
    }

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        Color color = getColor(player, args[2]);
        if (color == null) return false;
        selection.setGlowColor(color);
        selection.glow(player, 60);
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Glow color successfully set for selected display entity part(s) in your selection!", NamedTextColor.GREEN)));
        return true;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        Color color = getColor(player, args[2]);
        if (color == null) return false;
        if (selectedPart.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Interaction entities cannot have a glow color applied!", NamedTextColor.RED)));
            return false;
        }
        else{
            selectedPart.setGlowColor(color);
            selectedPart.glow(player, 60);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Glow color successfully set for your selected part!", NamedTextColor.GREEN)));
            return true;
        }
    }

    private Color getColor(Player player, String arg){
        Color c = ConversionUtils.getColorFromText(arg);
        if (c == null){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a valid color!", NamedTextColor.RED)));
            player.sendMessage(Component.text("/deu parts glowcolor <color | hex-code> [-all]", NamedTextColor.GRAY));
        }
        return c;
    }
}
