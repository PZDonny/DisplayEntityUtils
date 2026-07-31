package net.donnypz.displayentityutils.command.display;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePartSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.MultiPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.List;

class DisplayRotateCMD extends PartsSubCommand {

    private final String WORLD_SPACE = "-world";

    DisplayRotateCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("rotate", parentSubCommand, Permission.DISPLAY_TRANSFORM, true);
        setTabComplete(2, List.of("x", "y", "z"));
        setTabComplete(3, "<angle-in-degrees>");
        addFlag(WORLD_SPACE);
    }

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        try {
            float rotation = Float.parseFloat(args[3]);
            if (rotation == 0.0f) {
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a non-zero number for the rotation, in degrees!", NamedTextColor.RED)));
                return false;
            }
            Quaternionf q = getRotation(args[2], rotation);
            if (q == null) {
                incorrectUsage(player);
                return false;
            }

            boolean worldSpace = getOptionalArguments(player, args).hasFlag(WORLD_SPACE);
            for (ActivePart selectedPart : selection.getParts()) {
                if (!selectedPart.isDisplay()) continue;
                selectedPart.rotate(q, worldSpace);
            }
            player.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage
                    .miniMessage()
                    .deserialize(String.format("<green>Rotating your selected parts on their %s <yellow>%s <green>axis by <yellow>%s <green>degrees!",
                            worldSpace ? "world" : "local",
                            args[2].toUpperCase(),
                            rotation))));
            return true;
        } catch (NumberFormatException e) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a valid number for the scale!", NamedTextColor.RED)));
            return false;
        }
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (isNotDisplay(player, selectedPart)) return false;
        try {
            float rotation = Float.parseFloat(args[3]);
            if (rotation == 0.0f) {
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a non-zero number for the rotation, in degrees!", NamedTextColor.RED)));
                return false;
            }
            Quaternionf q = getRotation(args[2], rotation);
            if (q == null) {
                incorrectUsage(player);
                return false;
            }

            boolean worldSpace = getOptionalArguments(player, args).hasFlag(WORLD_SPACE);
            selectedPart.rotate(q, worldSpace);
            player.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage
                    .miniMessage()
                    .deserialize(String.format("<green>Rotating your selected part on the %s <yellow>%s <green>axis by <yellow>%s <green>degrees!",
                            worldSpace ? "world" : "local",
                            args[2].toUpperCase(),
                            rotation))));
            return true;
        } catch (NumberFormatException e) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a valid number for the scale!", NamedTextColor.RED)));
            return false;
        }
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
    }


    private Quaternionf getRotation(String arg, float angle) {
        float angleRad = (float) Math.toRadians(angle);
        if (arg.equalsIgnoreCase("x")) {
            return new Quaternionf().rotateX(angleRad);
        } else if (arg.equalsIgnoreCase("y")) {
            return new Quaternionf().rotateY(angleRad);
        } else if (arg.equalsIgnoreCase("z")) {
            return new Quaternionf().rotateZ(angleRad);
        }
        return null;
    }

    @Override
    protected String getDescription() {
        return "Rotate your selected display on an axis";
    }
}
