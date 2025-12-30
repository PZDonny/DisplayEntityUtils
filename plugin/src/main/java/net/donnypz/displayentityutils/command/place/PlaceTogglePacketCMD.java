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

class PlaceTogglePacketCMD extends PlayerSubCommand {
    PlaceTogglePacketCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("togglepacket", parentSubCommand, Permission.PLACE_TOGGLE_PACKET);
    }

    @Override
    public void execute(Player player, String[] args) {
        ItemStack heldItem = PlaceCMD.getHeldItem(player, true);
        if (heldItem == null) return;

        if (PlaceableGroupManager.isUsingPackets(heldItem)){
            PlaceableGroupManager.setUsePackets(heldItem, false);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Your held item's group will no longer spawn using packets", NamedTextColor.RED)));
        }
        else{
            PlaceableGroupManager.setUsePackets(heldItem, true);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Your held item's group will spawn using packets", NamedTextColor.GREEN)));
        }
    }
}
