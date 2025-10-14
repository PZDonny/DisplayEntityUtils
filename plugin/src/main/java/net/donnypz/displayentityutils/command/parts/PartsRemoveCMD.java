package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class PartsRemoveCMD extends PartsSubCommand {
    PartsRemoveCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("remove", parentSubCommand, Permission.PARTS_REMOVE, 2, 3);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected void executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        for (ActivePart part : selection.getSelectedParts()){
            if (part.isMaster()){
                continue;
            }
            if (part instanceof PacketDisplayEntityPart p){
                p.remove();
            }
            else if (part instanceof SpawnedDisplayEntityPart p){
                p.remove(true);
            }
        }
        player.sendMessage(Component.text("Successfully despawned all selected parts!", NamedTextColor.GREEN));
        removeGroupIfEmpty(player, group);
    }

    @Override
    protected void executeSinglePartAction(@NotNull Player player, @Nullable SpawnedDisplayEntityGroup group, @NotNull ActivePartSelection<?> selection, @NotNull SpawnedDisplayEntityPart selectedPart, @NotNull String[] args) {
        if (selectedPart.isMaster() && !selection.isSinglePartSelection()){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You cannot despawn the master/parent part!", NamedTextColor.RED)));
            return;
        }
        selectedPart.remove(true);
        player.sendMessage(Component.text("Successfully despawned your selected part!", NamedTextColor.GREEN));
        removePartSelectionIfEmpty(player, selection);
        removeGroupIfEmpty(player, group);
    }

    private void removePartSelectionIfEmpty(Player player, ActivePartSelection<?> selection){
        if (selection instanceof SpawnedPartSelection s && s.getSize() != 0) return;

        selection.remove();
        if (!(selection instanceof SinglePartSelection)){
            player.sendMessage(Component.text("Part selection reset! (No parts remaining)", NamedTextColor.RED));
        }
    }

    private void removeGroupIfEmpty(Player player, ActiveGroup<?> group){
        if (group == null) return;
        if (group.getParts().size() <= 1){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Despawning your group, not enough parts remain", NamedTextColor.YELLOW)));
            RelativePointUtils.removeRelativePoints(player);
            if (group instanceof SpawnedDisplayEntityGroup g){
                g.unregister(true, true);
            }
            else if (group instanceof PacketDisplayEntityGroup pg){
                pg.unregister();
            }
        }
    }
}
