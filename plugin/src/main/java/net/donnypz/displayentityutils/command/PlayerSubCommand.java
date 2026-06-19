package net.donnypz.displayentityutils.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class PlayerSubCommand extends DEUSubCommand {
    public PlayerSubCommand(@NotNull String commandName, @NotNull Permission permission) {
        super(commandName, permission);
    }

    public PlayerSubCommand(@NotNull String commandName, @NotNull DEUSubCommand parentSubCommand, @NotNull Permission permission) {
        super(commandName, parentSubCommand, permission);
    }

    public abstract void execute(Player sender, String[] args);

}
