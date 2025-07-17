package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;

class PartsBrightnessCMD extends PlayerSubCommand {
    PartsBrightnessCMD() {
        super(Permission.PARTS_BRIGHTNESS);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (DisplayGroupManager.getSelectedSpawnedGroup(player)== null){
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        SpawnedPartSelection selection = DisplayGroupManager.getPartSelection(player);
        if (selection == null){
            PartsCMD.noPartSelection(player);
            return;
        }

        if (args.length < 4) {
            player.sendMessage(Component.text("/mdis parts brightness <block> <sky> [-all]", NamedTextColor.RED));
            player.sendMessage(Component.text("| Brightness can any number between 0 and 15", NamedTextColor.GRAY));
            player.sendMessage(Component.text("| Set both \"block\" and \"sky\" to -1 to reset brightness", NamedTextColor.GRAY));
            return;
        }

        boolean isAll = args.length >= 5 && args[4].equalsIgnoreCase("-all");

        try{
            int block = Integer.parseInt(args[2]);
            int sky = Integer.parseInt(args[3]);
            if (sky > 15 || sky < 0 || block > 15 || block < 0){
                if (sky == -1 && block == -1){ //Reset Brightness
                    if (isAll){
                        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Brightness reset for part selection!", NamedTextColor.YELLOW)));
                        selection.setBrightness(null);
                    }
                    else{
                        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Brightness reset for your selected part!", NamedTextColor.YELLOW)));
                        selection.getSelectedPart().setBrightness(null);
                    }
                    return;
                }
                else{
                    throw new NumberFormatException();
                }
            }
            Display.Brightness brightness = new Display.Brightness(block, sky);
            if (isAll){
                selection.setBrightness(brightness);
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Brightness set for your part selection!", NamedTextColor.GREEN)));
            }
            else{
                selection.getSelectedPart().setBrightness(brightness);
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Brightness set for your selected part!", NamedTextColor.GREEN)));
            }
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Brightness values can only be whole numbers 0-15!", NamedTextColor.RED)));
            player.sendMessage(Component.text("Values of -1 for both block and sky will reset the brightness"));
        }

    }
}
