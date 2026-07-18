package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.command.gizmo.GizmoCMD;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class GroupPitchCMD extends GroupSubCommand {
    GroupPitchCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("pitch", parentSubCommand, Permission.GROUP_TRANSFORM, true);
        setTabComplete(2, "<pitch>");
        addFlag("-pivot");
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected void execute(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull String[] args) {
        if (RelativePointUtils.isViewingRelativePoints(player)){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You cannot play do that while viewing points!", NamedTextColor.RED)));
            return;
        }

        try{
            double oldPitch = group.getLocation().getPitch();
            float pitch = Float.parseFloat(args[2]);
            boolean pivot = getOptionalArguments(player, args).hasFlag("-pivot");
            group.setPitch(pitch, pivot);

            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Pitch set!", NamedTextColor.GREEN)));
            player.sendMessage(Component.text("| Old Pitch: "+oldPitch, NamedTextColor.GRAY));
            GizmoCMD.updateGizmoRotationIfExists(player);
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Please enter a valid number!", NamedTextColor.RED)));
        }
    }

    @Override
    protected String getDescription() {
        return "Set your selected group's pitch. \"-pivot\" pivots non-display entities around the group";
    }
}
