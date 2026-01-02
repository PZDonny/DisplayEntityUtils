package net.donnypz.displayentityutils.command.place;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.PlaceableGroupData;
import net.donnypz.displayentityutils.managers.PlaceableGroupManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

class PlaceSetCMD extends PlayerSubCommand {
    PlaceSetCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("set", parentSubCommand, Permission.PLACE_SET_ITEM);
        setTabComplete(2, "<group-tag>");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage /deu place set <group-tag>", NamedTextColor.RED)));
            return;
        }

        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (!PlaceableGroupManager.isValidItem(heldItem)){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You must be holding a block to do this command!", NamedTextColor.RED)));
            return;
        }

        String groupTag = args[2];

        new PlaceableGroupData(groupTag)
                .apply(heldItem);
        player.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Assigned a group to your held block <white>(Tag: "+groupTag+")")));
    }
}
