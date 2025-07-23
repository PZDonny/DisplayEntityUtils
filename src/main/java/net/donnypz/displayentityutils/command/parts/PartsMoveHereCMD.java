package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ServerSideSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class PartsMoveHereCMD extends PlayerSubCommand {

    PartsMoveHereCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("movehere", parentSubCommand, Permission.PARTS_TRANSFORM);
    }

    @Override
    public void execute(Player player, String[] args) {
        ServerSideSelection selection = DisplayGroupManager.getPartSelection(player);
        if (selection == null){
            DisplayEntityPluginCommand.noPartSelection(player);
            return;
        }

        if (PartsCMD.isUnwantedMultiSelection(player, selection)){
            return;
        }

        if (!selection.hasSelectedPart()){
            PartsCMD.invalidPartSelection(player);
            return;
        }
        Entity e = selection.getSelectedPart().getEntity();
        Location loc = player.getLocation();
        loc.setYaw(e.getYaw());
        loc.setPitch(e.getPitch());
        e.teleport(loc);
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Moved your selected part to your location!", NamedTextColor.GREEN)));
    }
}
