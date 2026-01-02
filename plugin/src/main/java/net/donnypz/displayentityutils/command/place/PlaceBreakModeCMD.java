package net.donnypz.displayentityutils.command.place;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.managers.PlaceableGroupManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

class PlaceBreakModeCMD extends PlayerSubCommand {
    PlaceBreakModeCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("breakmode", parentSubCommand, Permission.PLACE_BREAK_MODE);
    }

    @Override
    public void execute(Player player, String[] args) {
        DEUUser user = DEUUser.getOrCreateUser(player);
        if (user.isInPlacedGroupBreakMode()){
            user.setPlacedGroupBreakMode(false);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You have exited break-mode and can only break groups you have placed", NamedTextColor.RED)));
        }
        else{
            user.setPlacedGroupBreakMode(true);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You have entered break-mode and can break any placed groups", NamedTextColor.GREEN)));
        }
    }
}
