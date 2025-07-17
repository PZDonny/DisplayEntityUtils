package net.donnypz.displayentityutils.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class PlayerSubCommand extends DEUSubCommand {
    public PlayerSubCommand(@NotNull Permission permission) {
        super(permission);
    }

    public abstract void execute(Player sender, String[] args);

}
