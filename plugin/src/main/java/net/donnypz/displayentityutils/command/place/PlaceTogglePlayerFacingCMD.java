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

class PlaceTogglePlayerFacingCMD extends PlayerSubCommand {
    PlaceTogglePlayerFacingCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("toggleplayerfacing", parentSubCommand, Permission.PLACE_TOGGLE_FACING);
    }

    @Override
    public void execute(Player player, String[] args) {
        ItemStack heldItem = PlaceCMD.getHeldItem(player, true);
        if (heldItem == null) return;

        if (PlaceableGroupManager.isRespectingPlayerFacing(heldItem)){
            PlaceableGroupManager.setRespectPlayerFacing(heldItem, false);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Your held item's group will no longer respect the player's facing direction", NamedTextColor.RED)));
        }
        else{
            PlaceableGroupManager.setRespectPlayerFacing(heldItem, true);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Your held item's group will respect the player's facing direction", NamedTextColor.GREEN)));
        }
    }
}
