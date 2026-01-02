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

class PlaceToggleBlockFaceCMD extends PlayerSubCommand {
    PlaceToggleBlockFaceCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("toggleblockface", parentSubCommand, Permission.PLACE_TOGGLE_BLOCK_FACE);
    }

    @Override
    public void execute(Player player, String[] args) {
        ItemStack heldItem = PlaceCMD.getHeldItem(player, true);
        if (heldItem == null) return;

        if (PlaceableGroupManager.isRespectingBlockFace(heldItem)){
            PlaceableGroupManager.setRespectBlockFace(heldItem, false);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Your held item's group will no longer respect the block face its placed on", NamedTextColor.RED)));
        }
        else{
            PlaceableGroupManager.setRespectBlockFace(heldItem, true);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Your held item's group will respect the block face its placed on", NamedTextColor.GREEN)));
        }
    }
}
