package net.donnypz.displayentityutils.command;

import org.bukkit.entity.Player;

interface SubCommand {
    String permission = "";

    void execute(Player player, String[] args);

}
