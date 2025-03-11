package net.donnypz.displayentityutils.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

abstract class PlayerSubCommand extends SubCommand{
    PlayerSubCommand(@NotNull Permission permission) {
        super(permission);
    }

    abstract void execute(Player sender, String[] args);

}
