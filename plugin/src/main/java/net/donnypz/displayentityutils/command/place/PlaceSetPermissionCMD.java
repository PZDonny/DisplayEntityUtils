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

class PlaceSetPermissionCMD extends PlayerSubCommand {
    PlaceSetPermissionCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("setpermission", parentSubCommand, Permission.PLACE_SET_PERMISSION);
        setTabComplete(2, "<permission>");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage /deu place setpermission <permission>", NamedTextColor.RED)));
            return;
        }

        ItemStack heldItem = PlaceCMD.getHeldItem(player, true);
        if (heldItem == null) return;

        String permission = args[2];
        PlaceableGroupManager.setPlacePermission(heldItem, permission);
        player.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Successfully set the permission to place your held item's assigned group <white>(Permission: "+permission+")")));
    }
}
