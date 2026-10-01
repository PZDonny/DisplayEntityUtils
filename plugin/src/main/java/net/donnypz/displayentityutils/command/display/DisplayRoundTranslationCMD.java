package net.donnypz.displayentityutils.command.display;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePartSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.MultiPartSelection;
import net.donnypz.displayentityutils.utils.gizmo.GizmoSession;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

class DisplayRoundTranslationCMD extends PartsSubCommand {
    DisplayRoundTranslationCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("roundtranslation", parentSubCommand, Permission.DISPLAY_TRANSFORM, true);
        super.cancelIfDraggingGizmo();
        setTabComplete(2, List.of("x", "y", "z", "all"));
        addFlag("-resetpivot");
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        super.incorrectUsage(player);
    }

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        String arg = args[2].toLowerCase();
        if (!isValidArg(player, arg)) return false;

        selection.getParts().forEach(part -> roundTranslation(part, arg));
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Translation rounded down for all selected displays!", NamedTextColor.GREEN)));
        if (getOptionalArguments(player, args).hasFlag("-resetpivot")) resetGizmoPivot(player, selection);
        return true;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (isNotDisplay(player, selectedPart)) return false;

        String arg = args[2].toLowerCase();
        if (!isValidArg(player, arg)) return false;

        roundTranslation(selectedPart, arg);
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Rounded down selected display's translation!", NamedTextColor.GREEN)));
        if (getOptionalArguments(player, args).hasFlag("-resetpivot")) resetGizmoPivot(player, selection);
        return true;
    }

    boolean isValidArg(Player player, String arg){
        switch (arg) {
            case "x", "y", "z", "all" -> {
                return true;
            }
            default -> {
                sendIncorrectUsage(player);
                return false;
            }
        }
    }

    void roundTranslation(ActivePart part, String arg) {
        if (!part.isDisplay()) return;

        Vector3f translation = part.getTransformation().getTranslation();
        Vector roundRemove = new Vector(
                arg.equals("x") || arg.equals("all")
                        ? (int) translation.x - translation.x
                        : 0,
                arg.equals("y") || arg.equals("all")
                        ? (int) translation.y - translation.y
                        : 0,
                arg.equals("z") || arg.equals("all")
                        ? (int) translation.z - translation.z
                        : 0);
        if (roundRemove.isZero()) {
            return;
        }
        part.translate(roundRemove, 0, 0);
    }

    void resetGizmoPivot(Player player, ActivePartSelection<?> selection) {
        GizmoSession gizmo = DEUUser.getOrCreateUser(player).getGizmo();

        if (gizmo != null) {
            gizmo.teleport(selection.getLocation());
            player.sendMessage(Component.text("| Gizmo pivot reset", NamedTextColor.GRAY));
        }
    }


    @Override
    protected String getDescription() {
        return "Remove the decimal portion of your selected display's translation. (Ex: 12.25 would become 12.0). Optionally reset gizmo pivot with \"-resetpivot\"";
    }
}
