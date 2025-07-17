package net.donnypz.displayentityutils.command.interaction;

import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import org.bukkit.entity.Player;

class InteractionSpawnHereCMD extends PlayerSubCommand {
    InteractionSpawnHereCMD() {
        super(Permission.INTERACTION_SPAWN);
    }

    @Override
    public void execute(Player player, String[] args) {
        InteractionSpawnCMD.spawnForGroup(player, player.getLocation(), args);
    }
}