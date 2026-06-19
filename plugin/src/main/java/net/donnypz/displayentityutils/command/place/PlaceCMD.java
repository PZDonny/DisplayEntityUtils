package net.donnypz.displayentityutils.command.place;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.managers.PlaceableGroupManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlaceCMD extends ParentSubCommand {
    public PlaceCMD() {
        super("place");
        new PlaceSetCMD(this);
        new PlaceUnsetCMD(this);
        new PlaceSetPermissionCMD(this);
        new PlaceUnsetPermissionCMD(this);
        new PlaceTogglePlayerFacingCMD(this);
        new PlaceToggleBlockFaceCMD(this);
        new PlaceToggleDropItemCMD(this);
        new PlaceTogglePlacerOnlyCMD(this);
        new PlaceInfoCMD(this);
        new PlaceAddSoundCMD(this);
        new PlaceWhoPlacedCMD(this);
        new PlaceGetItemCMD(this);
        new PlaceBreakModeCMD(this);
    }

    static ItemStack getHeldItem(Player player, boolean mustBeAssigned){
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (!PlaceableGroupManager.isValidItem(heldItem)){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You must be holding a block to do this command!", NamedTextColor.RED)));
            return null;
        }

        if (mustBeAssigned && !PlaceableGroupManager.hasData(heldItem)){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Your held item does not have placeable group data!", NamedTextColor.RED)));
            return null;
        }
        return heldItem;
    }
}
