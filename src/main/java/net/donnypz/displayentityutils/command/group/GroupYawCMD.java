package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class GroupYawCMD extends PlayerSubCommand {
    GroupYawCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("yaw", parentSubCommand, Permission.GROUP_TRANSFORM);
    }

    @Override
    public void execute(Player player, String[] args) {

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        if (DEUCommandUtils.isViewingRelativePoints(player)){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("You cannot play do that while viewing points!", NamedTextColor.RED)));
            return;
        }

        if (args.length < 3) {
            player.sendMessage(Component.text("Incorrect Usage! /mdis group yaw <yaw> [-pivot]", NamedTextColor.RED));
            return;
        }

        try{
            float yaw = Float.parseFloat(args[2]);
            boolean pivot = false;
            if (args.length > 3){
                if (args[3].equalsIgnoreCase("-pivot")){
                    pivot = true;
                }
            }
            double oldYaw = group.getLocation().getYaw();
            group.setYaw(yaw, pivot);
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Yaw set!", NamedTextColor.GREEN)));
            player.sendMessage(Component.text("| Old Yaw: "+oldYaw, NamedTextColor.GRAY));
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Please enter a valid number!", NamedTextColor.RED)));
        }
    }
}
