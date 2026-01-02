package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.managers.PlaceableGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class GroupSelectPlacedCMD extends PlayerSubCommand {
    GroupSelectPlacedCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("selectplaced", parentSubCommand, Permission.GROUP_SELECT);
    }

    @Override
    public void execute(Player player, String[] args) {
        Block targetBlock = player.getTargetBlock(null, 15);
        PacketDisplayEntityGroup group = PlaceableGroupManager.getPlacedGroup(targetBlock);

        if (group == null){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You are not looking at a placed group's block! (Barrier)", NamedTextColor.RED)));
            return;
        }

        boolean selectResult = DisplayGroupManager.setSelectedGroup(player, group);
        if (selectResult){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Selected placed group!", NamedTextColor.GREEN)));
        }
        else{
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Failed to select placed group! Another player already has that group selected!", NamedTextColor.RED)));
        }

        int selectDuration = 50;
        group.glowAndMarkInteractions(player, selectDuration);
    }
}
