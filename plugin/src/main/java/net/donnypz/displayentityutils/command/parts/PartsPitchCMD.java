package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.command.gizmo.GizmoCMD;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePartSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.MultiPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class PartsPitchCMD extends PartsSubCommand {

    PartsPitchCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("pitch", parentSubCommand, Permission.PARTS_TRANSFORM);
        setTabComplete(2, "<pitch>");
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        return false;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (PartsCMD.isUnwantedMultiSelection(player, selection)) return false;

        try{
            float pitch = Float.parseFloat(args[2]);
            double oldPitch = selectedPart.getPitch();
            selectedPart.setPitch(pitch, false);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Pitch set!", NamedTextColor.GREEN)));
            player.sendMessage(Component.text("| Old Pitch: "+oldPitch, NamedTextColor.GRAY));
            GizmoCMD.updateGizmoRotationIfExists(player);
            return true;
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a valid number for the pitch!", NamedTextColor.RED)));
            return false;
        }
    }

    @Override
    protected String getDescription() {
        return "Set the pitch of an ungrouped part entity";
    }
}
