package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ServerSideSelection;
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
        ServerSideSelection sel = DisplayGroupManager.getPartSelection(player);
        if (sel == null){
            PartsCMD.noPartSelection(player);
            return;
        }

        SpawnedPartSelection partSelection = (SpawnedPartSelection) sel;
        if (PartsCMD.isUnwantedSingleSelection(player, sel)){
            return;
        }

        partSelection.reset();
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Part selection reset!", NamedTextColor.GREEN)));
    }
}
