package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.command.parts.PartsCMD;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ServerSideSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
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
        this.requireGroupSelection = false;
    }

    public PartsSubCommand(@NotNull String commandName, @NotNull DEUSubCommand parentSubCommand, @NotNull Permission permission, int minimumArgs, int allArgumentIndex, boolean requireGroupSelection) {
        super(commandName, parentSubCommand, permission);
        this.minimumArgs = minimumArgs;
        this.allArgumentIndex = allArgumentIndex;
        this.requireGroupSelection = requireGroupSelection;
    }

    @Override
    public void execute(Player player, String[] args){
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (requireGroupSelection && group == null){
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }
        ServerSideSelection selection = DisplayGroupManager.getPartSelection(player);
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

        if (args.length >= allArgumentIndex +1 && args[allArgumentIndex].equalsIgnoreCase("-all")){
            if (PartsCMD.isUnwantedSingleSelectionAll(player, selection)){
                return;
            }
            executeAllPartsAction(player, group, (SpawnedPartSelection) selection, args);
        }
        else{
            executeSinglePartAction(player, group, selection, selection.getSelectedPart(), args);
        }
    }

    protected abstract void sendIncorrectUsage(@NotNull Player player);

    protected abstract void executeAllPartsAction(@NotNull Player player, @Nullable SpawnedDisplayEntityGroup group, @NotNull SpawnedPartSelection selection, @NotNull String[] args);

    protected abstract void executeSinglePartAction(@NotNull Player player, @Nullable SpawnedDisplayEntityGroup group, @NotNull ServerSideSelection selection, @NotNull SpawnedDisplayEntityPart selectedPart, @NotNull String[] args);



}
