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

class PlaceToggleDropItemCMD extends PlayerSubCommand {
    PlaceToggleDropItemCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("toggledropitem", parentSubCommand, Permission.PLACE_TOGGLE_DROP_ITEM);
    }

    @Override
    public void execute(Player player, String[] args) {
        ItemStack heldItem = PlaceCMD.getHeldItem(player, true);
        if (heldItem == null) return;

        if (PlaceableGroupManager.isDropItem(heldItem)){
            PlaceableGroupManager.setDropItemOnBreak(heldItem, false);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Your held item's group will no longer drop the item used to place it", NamedTextColor.RED)));
        }
        else{
            PlaceableGroupManager.setDropItemOnBreak(heldItem, true);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Your held item's group will drop the item used to place it", NamedTextColor.GREEN)));
        }
    }
}
