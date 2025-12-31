package net.donnypz.displayentityutils.command.place;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.PlaceableGroupManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

class PlaceWhoPlacedCMD extends PlayerSubCommand {
    PlaceWhoPlacedCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("whoplaced", parentSubCommand, Permission.PLACE_WHO_PLACED);
    }

    @Override
    public void execute(Player player, String[] args) {
        ItemStack heldItem = PlaceCMD.getHeldItem(player, true);
        if (heldItem == null) return;

        Block targetBlock = player.getTargetBlock(null, 15);
        final UUID uuid = PlaceableGroupManager.getWhoPlaced(targetBlock);
        if (uuid == null){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You are not looking at a placed group's block! (Barrier)", NamedTextColor.RED)));
            return;
        }
        player.sendMessage(DisplayAPI.pluginPrefixLong);
        player.sendMessage(Component.text("Block Location: ")
                .append(Component.text(targetBlock.getX()+", "+targetBlock.getY()+", "+targetBlock.getZ(), NamedTextColor.YELLOW)));
        DisplayAPI.getScheduler().runAsync(() -> {
            OfflinePlayer plr = Bukkit.getOfflinePlayer(uuid);
            player.sendMessage(Component.text("Player: ")
                    .append(Component.text(plr.getName(), NamedTextColor.YELLOW)
                            .clickEvent(ClickEvent.copyToClipboard(plr.getName()))
                            .hoverEvent(HoverEvent.showText(Component.text("Click to copy")))));
            player.sendMessage(Component.text("UUID: ")
                    .append(Component.text(uuid.toString(), NamedTextColor.YELLOW)
                            .clickEvent(ClickEvent.copyToClipboard(uuid.toString()))
                            .hoverEvent(HoverEvent.showText(Component.text("Click to copy")))
                    ));
        });
    }
}
