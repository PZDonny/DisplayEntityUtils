package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

class GroupTranslateCMD implements SubCommand{
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

        if (args.length < 5){
            player.sendMessage(Component.text("/mdis group translate <direction> <distance> <tick-duration>", NamedTextColor.RED));
            return;
        }

        try{
            Direction direction = Direction.valueOf(args[2].toUpperCase());
            double distance = Double.parseDouble(args[3]);
            if (distance <= 0){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Enter a number greater than 0 for the distance!");
                return;
            }
            int duration = Integer.parseInt(args[4]);
            if (duration <= 0){
                duration = 0;
            }
            group.translate(direction, (float) distance, duration, -1);
            player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Translating spawned display entity group!");
        }
        catch(IllegalArgumentException e){
            if (e instanceof NumberFormatException){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Enter valid numbers!");
                player.sendMessage(Component.text("Duration must be a positive whole number, distance can be any positive number", NamedTextColor.GRAY, TextDecoration.ITALIC));
            }
            else{
                DisplayEntityPluginCommand.invalidDirection(player);
            }
        }
    }
}
