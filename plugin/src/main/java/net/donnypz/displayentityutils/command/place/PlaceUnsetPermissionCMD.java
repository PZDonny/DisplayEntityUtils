package net.donnypz.displayentityutils.command.place;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.PlaceableGroupManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

class PlaceUnsetPermissionCMD extends PlayerSubCommand {
    PlaceUnsetPermissionCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("unsetpermission", parentSubCommand, Permission.PLACE_SET_PERMISSION);
    }

    @Override
    public void execute(Player player, String[] args) {
        ItemStack heldItem = PlaceCMD.getHeldItem(player, true);
        if (heldItem == null) return;

        if (!PlaceableGroupManager.hasPlacePermission(heldItem)) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Your held item does not have a set permission to place its group!", NamedTextColor.RED)));
            return;
        }
        PlaceableGroupManager.setPlacePermission(heldItem, null);
        player.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<yellow>Removed the set permission to your held item's group.")));
    }
}
