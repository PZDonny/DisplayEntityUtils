package net.donnypz.displayentityutils.command;

import org.jetbrains.annotations.NotNull;

abstract class SubCommand {
    Permission permission;

    SubCommand(@NotNull Permission permission){
        this.permission = permission;
    }

    public Permission getPermission() {
        return permission;
    }
}
