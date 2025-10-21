package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.command.parts.PartsCMD;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PartsSubCommand extends PlayerSubCommand {
    int minimumArgs;
    int allArgumentIndex;
    boolean requireGroupSelection;

    public PartsSubCommand(@NotNull String commandName, @NotNull DEUSubCommand parentSubCommand, @NotNull Permission permission, int minimumArgs, int allArgumentIndex) {
        super(commandName, parentSubCommand, permission);
        this.minimumArgs = minimumArgs;
        this.allArgumentIndex = allArgumentIndex;
        setTabComplete(allArgumentIndex, "-all");
        this.requireGroupSelection = false;
    }

    @Override
    public void execute(Player player, String[] args){
        ActiveGroup<?> group = DisplayGroupManager.getSelectedGroup(player);
        if (requireGroupSelection && group == null){
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }
        ActivePartSelection<?> selection = DisplayGroupManager.getPartSelection(player);
        if (selection == null){
            DisplayEntityPluginCommand.noPartSelection(player);
            return;
        }

        if (args.length < minimumArgs){
            sendIncorrectUsage(player);
            return;
        }

        if (!selection.hasSelectedPart()){
            PartsCMD.invalidPartSelection(player);
            return;
        }

        boolean updatePacket;
        if (args.length >= allArgumentIndex +1 && args[allArgumentIndex].equalsIgnoreCase("-all")){
            if (PartsCMD.isUnwantedSingleSelectionAll(player, selection)){
                return;
            }
            updatePacket = executeAllPartsAction(player, group, (MultiPartSelection<?>) selection, args);
        }
        else{
            updatePacket = executeSinglePartAction(player, group, selection, selection.getSelectedPart(), args);
        }

        if (updatePacket && group instanceof PacketDisplayEntityGroup pg && pg.getMasterPart() != null){
            pg.refresh();
        }
    }

    protected abstract void sendIncorrectUsage(@NotNull Player player);

    protected abstract boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args);

    protected abstract boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args);
}
