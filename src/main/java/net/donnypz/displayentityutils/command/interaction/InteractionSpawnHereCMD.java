package net.donnypz.displayentityutils.command.interaction;

import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class InteractionSpawnHereCMD extends PlayerSubCommand {
    InteractionSpawnHereCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("spawnhere", parentSubCommand, Permission.INTERACTION_SPAWN);
    }

    @Override
    public void execute(Player player, String[] args) {
        InteractionSpawnCMD.spawnForGroup(player, player.getLocation(), args);
    }
}