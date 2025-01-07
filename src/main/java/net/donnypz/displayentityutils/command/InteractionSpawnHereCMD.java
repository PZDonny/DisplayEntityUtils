package net.donnypz.displayentityutils.command;

import org.bukkit.entity.Player;

class InteractionSpawnHereCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        InteractionSpawnCMD.spawnForGroup(player, player.getLocation(), args);
    }
}