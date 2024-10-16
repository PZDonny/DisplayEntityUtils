package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

class GroupScaleCMD implements SubCommand{
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

        if (args.length < 4) {
            player.sendMessage(Component.text("/mdis group scale <scale-multiplier> <tick-duration>", NamedTextColor.RED));
            return;
        }

        try{
            float multiplier = Float.parseFloat(args[2]);
            if (multiplier <= 0){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Enter a number greater than 0 for the scale multiplier!");
                return;
            }
            int duration = Integer.parseInt(args[3]);
            if (duration < 0){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Enter a whole number, 0 or greater, for the duration!");
                return;
            }
            player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Scaling spawned display entity group!");
            player.sendMessage(Component.text("Old Scale: "+group.getScaleMultiplier()+"x", NamedTextColor.GRAY));
            player.sendMessage(Component.text("New Scale: "+multiplier+"x", NamedTextColor.YELLOW));
            group.scale(multiplier, duration, true);
        }
        catch(IllegalArgumentException e){
            if (e instanceof NumberFormatException){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Enter valid numbers!");
            }
        }
    }
}
