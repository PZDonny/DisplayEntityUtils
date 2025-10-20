package net.donnypz.displayentityutils.command.interaction;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class InteractionSpawnHereCMD extends PlayerSubCommand {
    InteractionSpawnHereCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("spawnhere", parentSubCommand, Permission.INTERACTION_SPAWN);
        setTabComplete(2, "<height>");
        setTabComplete(3, "<width>");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 4){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect Usage! /mdis interaction spawnhere <height> <width>", NamedTextColor.RED)));
            return;
        }

        InteractionSpawnCMD.spawnInteraction(player, player.getLocation(), args);
    }
}