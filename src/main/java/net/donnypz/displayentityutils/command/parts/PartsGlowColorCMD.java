package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ServerSideSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class PartsGlowColorCMD extends PartsSubCommand {
    PartsGlowColorCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("glowcolor", parentSubCommand, Permission.PARTS_GLOW_COLOR, 3, 3);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter a valid color!", NamedTextColor.RED)));
        player.sendMessage(Component.text("/mdis parts glowcolor <color | hex-code> [-all]", NamedTextColor.GRAY));
    }

    @Override
    protected void executeAllPartsAction(@NotNull Player player, @Nullable SpawnedDisplayEntityGroup group, @NotNull SpawnedPartSelection selection, @NotNull String[] args) {
        Color color = getColor(player, args[2]);
        if (color == null) return;
        selection.setGlowColor(color);
        selection.glow(player, 60);
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Glow color successfully set for selected display entity part(s) in your selection!", NamedTextColor.GREEN)));
    }

    @Override
    protected void executeSinglePartAction(@NotNull Player player, @Nullable SpawnedDisplayEntityGroup group, @NotNull ServerSideSelection selection, @NotNull SpawnedDisplayEntityPart selectedPart, @NotNull String[] args) {
        Color color = getColor(player, args[2]);
        if (color == null) return;
        if (selectedPart.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Interaction entities cannot have a glow color applied!", NamedTextColor.RED)));
        }
        else{
            selectedPart.setGlowColor(color);
            selectedPart.glow(player, 60);
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Glow color successfully set for your selected part!", NamedTextColor.GREEN)));
        }
    }

    private Color getColor(Player player, String arg){
        Color c = DEUCommandUtils.getColorFromText(arg);
        if (c == null){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter a valid color!", NamedTextColor.RED)));
            player.sendMessage(Component.text("/mdis parts glowcolor <color | hex-code> [-all]", NamedTextColor.GRAY));
        }
        return c;
    }
}
