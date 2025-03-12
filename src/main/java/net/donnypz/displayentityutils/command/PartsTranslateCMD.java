package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

class PartsTranslateCMD extends PlayerSubCommand {
    PartsTranslateCMD() {
        super(Permission.PARTS_TRANSLATE);
    }

    @Override
    public void execute(Player player, String[] args) {

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(player);
        if (partSelection == null){
            PartsCMD.noPartSelection(player);
            return;
        }

        if (args.length < 5){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("/mdis parts translate <direction> <distance> <tick-duration> [-all]", NamedTextColor.RED)));
            return;
        }

        try{
            Direction direction = Direction.valueOf(args[2].toUpperCase());
            float distance = Float.parseFloat(args[3]);
            if (distance <= 0){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter a number greater than 0 for the distance!", NamedTextColor.RED)));
                return;
            }
            int duration = Integer.parseInt(args[4]);
            if (duration <= 0){
                duration = 0;
            }
            if (args.length >= 6 && args[5].equalsIgnoreCase("-all")){
                partSelection.translate(distance, duration, -1, direction);
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Translating all selected parts!", NamedTextColor.GREEN)));
            }
            else{
                SpawnedDisplayEntityPart selected = partSelection.getSelectedPart();
                selected.translate(distance, duration, -1, direction);
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Translating your selected part!", NamedTextColor.GREEN)));
            }

        }
        catch(IllegalArgumentException e){
            if (e instanceof NumberFormatException){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter valid numbers!", NamedTextColor.RED)));
                player.sendMessage(Component.text("Duration must be a positive whole number, distance can be any positive number", NamedTextColor.GRAY, TextDecoration.ITALIC));
            }
            else{
                DisplayEntityPluginCommand.invalidDirection(player);
            }
        }



    }

}
