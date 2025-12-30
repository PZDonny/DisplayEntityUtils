package net.donnypz.displayentityutils.command.place;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.PlaceableGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.DEUSound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class PlaceInfoCMD extends PlayerSubCommand {
    PlaceInfoCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("info", parentSubCommand, Permission.PLACE_INFO);
    }

    @Override
    public void execute(Player player, String[] args) {
        ItemStack heldItem = PlaceCMD.getHeldItem(player, true);
        if (heldItem == null) return;

        player.sendMessage(DisplayAPI.pluginPrefixLong);

        String groupTag = PlaceableGroupManager.getGroupTag(heldItem);
        String permission = PlaceableGroupManager.getPlacePermission(heldItem);
        if (permission == null){
            permission = "<gray>NOT SET";
        }
        boolean respectPlayerFacing = PlaceableGroupManager.isRespectingPlayerFacing(heldItem);
        boolean respectBlockFace = PlaceableGroupManager.isRespectingBlockFace(heldItem);
        boolean packetBased = PlaceableGroupManager.isUsingPackets(heldItem);
        List<DEUSound> placeSounds = PlaceableGroupManager.getSounds(heldItem, true);
        List<DEUSound> breakSounds = PlaceableGroupManager.getSounds(heldItem, false);

        player.sendMessage(MiniMessage.miniMessage().deserialize("Group Tag: <yellow>"+groupTag));
        player.sendMessage(MiniMessage.miniMessage().deserialize("Place Permission: <yellow>"+permission));
        player.sendMessage(MiniMessage.miniMessage().deserialize("Respect Player Facing: "+(respectPlayerFacing ? "<green>ENABLED" : "<red>DISABLED")));
        player.sendMessage(MiniMessage.miniMessage().deserialize("Respect Block Face: "+(respectBlockFace ? "<green>ENABLED" : "<red>DISABLED")));
        player.sendMessage(MiniMessage.miniMessage().deserialize("Packet Based: "+(packetBased ? "<green>TRUE" : "<red>FALSE")));

        DEUSound.sendInfo(placeSounds, player, "Place Sounds",
                player.hasPermission(Permission.PLACE_SOUND.getPermission())
                ? s ->
                    {
                        PlaceableGroupManager.removeSound(heldItem, s, true);
                    }
                : null);

        DEUSound.sendInfo(breakSounds, player, "Break Sounds",
                player.hasPermission(Permission.PLACE_SOUND.getPermission())
                ? s ->
                    {
                        PlaceableGroupManager.removeSound(heldItem, s, false);
                    }
                : null);
    }
}