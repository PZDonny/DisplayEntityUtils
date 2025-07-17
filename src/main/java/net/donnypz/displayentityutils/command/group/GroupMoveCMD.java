package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

class GroupMoveCMD extends PlayerSubCommand {
    GroupMoveCMD() {
        super(Permission.GROUP_TRANSFORM);
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
