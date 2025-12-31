package net.donnypz.displayentityutils.command.place;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.PlaceableGroupManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

class PlaceGetItemCMD extends PlayerSubCommand {
    PlaceGetItemCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("getitem", parentSubCommand, Permission.PLACE_GET_ITEM);
    }

    @Override
    public void execute(Player player, String[] args) {
        ItemStack heldItem = PlaceCMD.getHeldItem(player, true);
        if (heldItem == null) return;


        Block targetBlock = player.getTargetBlock(null, 15);
        final ItemStack itemStack = PlaceableGroupManager.getItemStack(targetBlock);
        if (itemStack == null){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You are not looking at a placed group's block! (Barrier)", NamedTextColor.RED)));
            return;
        }

        player.give(itemStack);
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You have been given the item to place the group", NamedTextColor.GREEN)));
    }
}
