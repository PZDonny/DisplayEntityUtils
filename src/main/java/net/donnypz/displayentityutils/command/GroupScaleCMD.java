package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

class GroupScaleCMD extends PlayerSubCommand {
    GroupScaleCMD() {
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

        if (args.length < 4) {
            player.sendMessage(Component.text("/mdis group scale <scale-multiplier> <tick-duration>", NamedTextColor.RED));
            return;
        }

        try{
            float multiplier = Float.parseFloat(args[2]);
            if (multiplier <= 0){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter a number greater than 0 for the scale multiplier!", NamedTextColor.RED)));
                return;
            }
            int duration = Integer.parseInt(args[3]);
            if (duration < 0){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter a whole number, 0 or greater, for the duration!", NamedTextColor.RED)));
                return;
            }
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Scaling spawned display entity group!", NamedTextColor.GREEN)));
            player.sendMessage(Component.text("Old Scale: "+group.getScaleMultiplier()+"x", NamedTextColor.GRAY));
            player.sendMessage(Component.text("New Scale: "+multiplier+"x", NamedTextColor.YELLOW));
            group.scale(multiplier, duration, true);
        }
        catch(IllegalArgumentException e){
            if (e instanceof NumberFormatException){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter valid numbers!", NamedTextColor.RED)));
            }
        }
    }
}
