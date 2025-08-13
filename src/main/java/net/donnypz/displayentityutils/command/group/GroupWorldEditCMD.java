package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.WorldUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class GroupWorldEditCMD extends PlayerSubCommand {
    GroupWorldEditCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("wetogroup", parentSubCommand, Permission.GROUP_WORLD_EDIT);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPlugin.isWorldEditInstalled()){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("This command requires WorldEdit to be installed on this server!", NamedTextColor.RED)));
            return;
        }

        boolean removeBlocks = args.length >= 3 && args[2].equals("-remove");
        SpawnedDisplayEntityGroup g = WorldUtils.createGroupFromWorldEditSelection(player, removeBlocks);
        if (g == null){
            player.sendMessage(Component.text("Failed to convert WorldEdit selection to a spawned group! Ensure that:", NamedTextColor.RED));
            player.sendMessage(Component.text("- Your selection is valid", NamedTextColor.GRAY));
            player.sendMessage(Component.text("- Your selection does not consist only of air blocks", NamedTextColor.GRAY));
            player.sendMessage(Component.text("- You are in your selection's world", NamedTextColor.GRAY));
            return;
        }

        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Converted your WorldEdit selection to a spawned group!", NamedTextColor.GREEN)));
        player.sendMessage(Component.text("| Your group selection has been changed to the newly created group (Origin at your location)", NamedTextColor.GRAY, TextDecoration.ITALIC));

        DisplayGroupManager.setSelectedSpawnedGroup(player, g);
        g.glow(player, 60);
        if (removeBlocks) player.sendMessage(Component.text("| Selected blocks have been removed!", NamedTextColor.YELLOW, TextDecoration.ITALIC));
    }
}
