package net.donnypz.displayentityutils.command;

import org.bukkit.entity.Player;

interface SubCommand {
    void execute(Player player, String[] args);

}
