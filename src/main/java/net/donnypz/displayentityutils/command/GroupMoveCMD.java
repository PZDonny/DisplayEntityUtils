package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

class GroupMoveCMD implements PlayerSubCommand {
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

        if (args.length < 5) {
            player.sendMessage(Component.text("/mdis group move <direction> <distance> <tick-duration>", NamedTextColor.RED));
            return;
        }

        try{
            Direction direction = Direction.valueOf(args[2].toUpperCase());
            double distance = Double.parseDouble(args[3]);
            if (distance <= 0){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter a number greater than 0 for the distance!", NamedTextColor.RED)));
                return;
            }
            int duration = Integer.parseInt(args[4]);
            if (duration <= 0){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter a whole number greater than 0 for the duration!", NamedTextColor.RED)));
                return;
            }
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Moving spawned display entity group!", NamedTextColor.GREEN)));
            group.teleportMove(direction, distance, duration);
        }
        catch(IllegalArgumentException e){
            if (e instanceof NumberFormatException){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter valid numbers!", NamedTextColor.RED)));
            }
            else{
                DisplayEntityPluginCommand.invalidDirection(player);
            }
        }
    }
}
