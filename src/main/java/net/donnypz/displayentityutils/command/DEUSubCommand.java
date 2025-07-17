package net.donnypz.displayentityutils.command;

import org.jetbrains.annotations.NotNull;

public abstract class DEUSubCommand {
    Permission permission;

    DEUSubCommand(@NotNull Permission permission){
        this.permission = permission;
    }

    public Permission getPermission() {
        return permission;
    }
}
