package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class GroupBrightnessCMD extends PlayerSubCommand {
    GroupBrightnessCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("brightness", parentSubCommand, Permission.GROUP_BRIGHTNESS);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        if (args.length < 4) {
            player.sendMessage(Component.text("/mdis group brightness <block> <sky>", NamedTextColor.RED));
            player.sendMessage(Component.text("| Brightness can any number between 0 and 15", NamedTextColor.GRAY));
            player.sendMessage(Component.text("| Set both \"block\" and \"sky\" to -1 to reset brightness", NamedTextColor.GRAY));
            return;
        }

        try{
            int block = Integer.parseInt(args[2]);
            int sky = Integer.parseInt(args[3]);
            if (sky > 15 || sky < 0 || block > 15 || block < 0){
                if (sky == -1 && block == -1){ //Reset Brightness
                    group.setBrightness(null);
                    player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Brightness reset!", NamedTextColor.YELLOW)));
                    return;
                }
                else{
                    throw new NumberFormatException();
                }
            }
            group.setBrightness(new Display.Brightness(block, sky));
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Brightness set!", NamedTextColor.GREEN)));
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Brightness values can only be whole numbers 0-15!", NamedTextColor.RED)));
            player.sendMessage(Component.text("Values of -1 for both block and sky will reset the brightness"));
        }
    }
}
