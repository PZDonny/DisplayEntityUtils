package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.parts.PartsCMD;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class PartsSubCommand extends PlayerSubCommand {
    int minimumArgs;
    boolean requireGroupSelection = false;

    public PartsSubCommand(@NotNull String commandName, @NotNull DEUSubCommand parentSubCommand, @NotNull Permission permission) {
        super(commandName, parentSubCommand, permission);
        this.minimumArgs = -1;
    }

    public PartsSubCommand(@NotNull String commandName, @NotNull DEUSubCommand parentSubCommand, @NotNull Permission permission, boolean allIsFlag) {
        this(commandName, parentSubCommand, permission, -1, allIsFlag);
    }

    public PartsSubCommand(@NotNull String commandName, @NotNull DEUSubCommand parentSubCommand, @NotNull Permission permission, int minimumArgs, boolean allIsFlag) {
        super(commandName, parentSubCommand, permission);
        this.minimumArgs = minimumArgs;
        if (allIsFlag){
            addFlag("-all");
        }
        else{
            addOption("-all", List.of("on", "off"));
        }
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

        if (minimumArgs == -1 && !hasMinimumArguments(player, args)) return;
        if (args.length < minimumArgs){
            sendIncorrectUsage(player);
            return;
        }

        if (!selection.hasSelectedPart()){
            PartsCMD.invalidPartSelection(player);
            return;
        }

        boolean updatePacket;
        OptionalArguments oArgs = getOptionalArguments(player, args);
        if (!oArgs.isValidOptions()) return;

        if (oArgs.hasFlag("-all") || !oArgs.getOption("-all").isBlank()){
            if (PartsCMD.isUnwantedSingleSelectionAll(player, selection)){
                return;
            }
            updatePacket = executeAllPartsAction(player, group, (MultiPartSelection<?>) selection, args);
        }
        else{
            updatePacket = executeSinglePartAction(player, group, selection, selection.getSelectedPart(), args);
        }

        if (updatePacket && group instanceof PacketDisplayEntityGroup pg && pg.getMasterPart() != null){
            pg.update();
        }
    }

    protected abstract void sendIncorrectUsage(@NotNull Player player);

    protected abstract boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args);

    protected abstract boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args);

    protected boolean isInvalidType(Player player, ActivePart part, SpawnedDisplayEntityPart.PartType validType){
        if (part.getType() != validType){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You can only do this with "+validType.name().toLowerCase().replace("_"," ")+" entities!", NamedTextColor.RED)));
            return true;
        }
        return false;
    }

    protected boolean isNotDisplay(Player player, ActivePart part){
        if (!part.isDisplay()){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You can only do this with display entities!", NamedTextColor.RED)));
            return true;
        }
        return false;
    }
}
