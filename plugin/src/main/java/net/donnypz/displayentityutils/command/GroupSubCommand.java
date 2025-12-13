package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GroupSubCommand extends PlayerSubCommand{
    int minimumArgs;
    boolean requireGroupSelection;
    boolean refreshIfPersistentPacket;

    public GroupSubCommand(@NotNull String commandName, @NotNull DEUSubCommand parentSubCommand, @NotNull Permission permission, int minimumArgs, boolean refreshIfPersistentPacket) {
        super(commandName, parentSubCommand, permission);
        this.minimumArgs = minimumArgs;
        this.requireGroupSelection = true;
        this.refreshIfPersistentPacket = refreshIfPersistentPacket;
    }

    @Override
    public final void execute(Player player, String[] args) {
        ActiveGroup<?> group = DisplayGroupManager.getSelectedGroup(player);
        if (requireGroupSelection && group == null){
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        if (args.length < minimumArgs){
            sendIncorrectUsage(player);
            return;
        }

        execute(player, group, args);
        if (refreshIfPersistentPacket){
            if (group instanceof PacketDisplayEntityGroup pg && pg.isPersistent()){
                pg.update();
            }
        }
    }

    protected abstract void sendIncorrectUsage(@NotNull Player player);

    protected abstract void execute(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull String[] args);
}
