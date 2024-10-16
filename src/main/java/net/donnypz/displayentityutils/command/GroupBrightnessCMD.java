package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;

class GroupBrightnessCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.GROUP_BRIGHTNESS)){
            return;
        }

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        if (args.length < 4) {
            player.sendMessage(Component.text("/mdis group brightness <block> <sky>", NamedTextColor.RED));
            player.sendMessage(Component.text("Set both \"block\" and \"sky\" to -1 to reset brightness"));
            return;
        }

        try{
            int block = Integer.parseInt(args[2]);
            int sky = Integer.parseInt(args[3]);
            if (sky > 15 || sky < 0 || block > 15 || block < 0){
                if (sky == -1 && block == -1){ //Reset Brightness
                    group.setBrightness(null);
                    player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.YELLOW+"Brightness reset!");
                    return;
                }
                else{
                    throw new NumberFormatException();
                }
            }
            group.setBrightness(new Display.Brightness(block, sky));
            player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Brightness set!");
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Brightness values can only be whole numbers 0-15!");
            player.sendMessage(Component.text("Values of -1 for both block and sky will reset the brightness"));
        }
    }
}
