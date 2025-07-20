package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class PartsResetCMD extends PlayerSubCommand {
    PartsResetCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("reset", parentSubCommand, Permission.PARTS_SELECT);
    }

    @Override
    public void execute(Player player, String[] args) {

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        SpawnedPartSelection selection = DisplayGroupManager.getPartSelection(player);
        if (selection == null){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("You do not have a part selection!", NamedTextColor.YELLOW)));
            return;
        }
        selection.reset();
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Part selection reset!", NamedTextColor.GREEN)));
    }
}
