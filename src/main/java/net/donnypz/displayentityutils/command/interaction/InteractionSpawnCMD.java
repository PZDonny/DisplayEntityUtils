package net.donnypz.displayentityutils.command.interaction;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class InteractionSpawnCMD extends PlayerSubCommand {
    InteractionSpawnCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("spawn", parentSubCommand, Permission.INTERACTION_SPAWN);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        if (args.length < 4){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Incorrect Usage! /mdis interaction spawn <height> <width>", NamedTextColor.RED)));
            return;
        }

        Interaction i = spawnInteraction(player, group.getLocation(), args);
        if (i == null) return;
        group.addInteractionEntity(i);
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("| The interaction has been added to your group!", NamedTextColor.YELLOW)));
        DisplayEntityPluginCommand.suggestUpdateSelection(player);
    }

    static void spawnForGroup(Player player, Location spawnLocation, String[] args){

    }

    static Interaction spawnInteraction(Player player, Location spawnLoc, String[] args){
        try{

            float height = Float.parseFloat(args[2]);
            float width = Float.parseFloat(args[3]);

            Interaction interaction = spawnLoc.getWorld().spawn(spawnLoc, Interaction.class, i -> {
                i.setInteractionHeight(height);
                i.setInteractionWidth(width);
            });
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Spawned a new Interaction entity", NamedTextColor.GREEN)));
            player.sendMessage(Component.text("| Height: "+height, NamedTextColor.GRAY));
            player.sendMessage(Component.text("| Width: "+width, NamedTextColor.GRAY));
            return interaction;
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter valid numbers!", NamedTextColor.RED)));
            return null;
        }
    }
}