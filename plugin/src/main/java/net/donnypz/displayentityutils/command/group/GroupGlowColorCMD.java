package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.ConversionUtils;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class GroupGlowColorCMD extends GroupSubCommand {
    GroupGlowColorCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("glowcolor", parentSubCommand, Permission.GROUP_GLOW_COLOR, 3, true);
        setTabComplete(2, TabSuggestion.COLORS);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a valid color!", NamedTextColor.RED)));
        player.sendMessage(Component.text("/mdis group glowcolor <color | hex-code>", NamedTextColor.GRAY));
    }

    @Override
    protected void execute(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull String[] args) {
        Color c = ConversionUtils.getColorFromText(args[2]);
        if (c == null){
            sendIncorrectUsage(player);
            return;
        }
        group.setGlowColor(c);
        group.glow(player, 60);
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Glow color successfully set for display entity group!", NamedTextColor.GREEN)));
    }

}
