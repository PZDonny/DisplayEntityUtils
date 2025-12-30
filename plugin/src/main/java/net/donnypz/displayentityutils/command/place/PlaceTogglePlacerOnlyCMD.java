package net.donnypz.displayentityutils.command.place;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.PlaceableGroupManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

class PlaceTogglePlacerOnlyCMD extends PlayerSubCommand {
    PlaceTogglePlacerOnlyCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("toggleplaceronly", parentSubCommand, Permission.PLACE_TOGGLE_PLACER_ONLY);
    }

    @Override
    public void execute(Player player, String[] args) {
        ItemStack heldItem = PlaceCMD.getHeldItem(player, true);
        if (heldItem == null) return;

        if (PlaceableGroupManager.isPlacerBreaksOnly(heldItem)){
            PlaceableGroupManager.setPlacerBreaksOnly(heldItem, false);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Your held item's group can only broken by any player", NamedTextColor.RED)));
        }
        else{
            PlaceableGroupManager.setPlacerBreaksOnly(heldItem, true);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Your held item's group can only be broken by the player who placed it", NamedTextColor.GREEN)));
        }
    }
}
