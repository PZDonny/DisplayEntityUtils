package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

class GroupYawCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.GROUP_TRANSFORM)){
            return;
        }

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
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
