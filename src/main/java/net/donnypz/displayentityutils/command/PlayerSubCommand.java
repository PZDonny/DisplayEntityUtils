package net.donnypz.displayentityutils.command;

import org.bukkit.entity.Player;

interface PlayerSubCommand extends SubCommand{
    void execute(Player player, String[] args);

}
