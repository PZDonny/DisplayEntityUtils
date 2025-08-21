package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.ConversionUtils;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class GroupGlowColorCMD extends PlayerSubCommand {
    GroupGlowColorCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("glowcolor", parentSubCommand, Permission.GROUP_GLOW_COLOR);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        if (args.length < 3) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a valid color!", NamedTextColor.RED)));
            player.sendMessage(Component.text("/mdis group glowcolor <color | hex-code>", NamedTextColor.GRAY));
            return;
        }

        Color c = ConversionUtils.getColorFromText(args[2]);
        if (c == null){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a valid color!", NamedTextColor.RED)));
            player.sendMessage(Component.text("/mdis group glowcolor <color | hex-code>", NamedTextColor.GRAY));
            return;
        }
        group.setGlowColor(c);
        group.glow(player, 60);
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Glow color successfully set for display entity group!", NamedTextColor.GREEN)));
    }

}
