package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

class GroupGlowColorCMD extends PlayerSubCommand {
    GroupGlowColorCMD() {
        super(Permission.GROUP_GLOW_COLOR);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        if (args.length < 3) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter a valid color!", NamedTextColor.RED)));
            player.sendMessage(Component.text("/mdis group glowcolor <color | hex-code>", NamedTextColor.GRAY));
            return;
        }

        Color c = DEUCommandUtils.getColorFromText(args[2]);
        if (c == null){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter a valid color!", NamedTextColor.RED)));
            player.sendMessage(Component.text("/mdis group glowcolor <color | hex-code>", NamedTextColor.GRAY));
            return;
        }
        group.setGlowColor(c);
        group.glow(player, 60);
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Glow color successfully set for display entity group!", NamedTextColor.GREEN)));
    }

}
