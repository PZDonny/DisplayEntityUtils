package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;

class InteractionSpawnCMD implements PlayerSubCommand {
    @Override
    public void execute(Player player, String[] args) {
        spawnForGroup(player, null, args);
    }

    static void spawnForGroup(Player player, Location spawnLocation, String[] args){
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.INTERACTION_SPAWN)){
            return;
        }
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }
        if (spawnLocation == null){
            spawnLocation = group.getLocation();
        }

        if (args.length < 4){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Incorrect Usage! /mdis interaction spawn <height> <width>", NamedTextColor.RED)));
            return;
        }

        try{
            float height = Float.parseFloat(args[2]);
            float width = Float.parseFloat(args[3]);
            Interaction interaction = spawnLocation.getWorld().spawn(spawnLocation, Interaction.class, i -> {
                i.setInteractionHeight(height);
                i.setInteractionWidth(width);
            });
            group.addInteractionEntity(interaction);
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Added Spawned Interaction entity to your selected group!", NamedTextColor.GREEN)));
            player.sendMessage(Component.text("| Height: "+height, NamedTextColor.GRAY));
            player.sendMessage(Component.text("| Width: "+width, NamedTextColor.GRAY));
            DisplayEntityPluginCommand.suggestUpdateSelection(player);
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter valid numbers!", NamedTextColor.RED)));
        }
    }
}