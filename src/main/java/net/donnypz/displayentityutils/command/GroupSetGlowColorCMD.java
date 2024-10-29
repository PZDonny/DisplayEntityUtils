package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.deu.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

class GroupSetGlowColorCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.GROUP_GLOW_COLOR_SET)){
            return;
        }

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        if (args.length < 3) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter a valid color!", NamedTextColor.RED)));
            player.sendMessage(Component.text("/mdis group setglowcolor <color | hex-code>", NamedTextColor.GRAY));
            return;
        }

        Color c = DEUCommandUtils.getColorFromText(args[2]);
        if (c == null){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter a valid color!", NamedTextColor.RED)));
            player.sendMessage(Component.text("/mdis group setglowcolor <color | hex-code>", NamedTextColor.GRAY));
            return;
        }
        group.setGlowColor(c);
        group.glow(60, true);
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Glow color successfully set for display entity group!", NamedTextColor.GREEN)));
    }

}
