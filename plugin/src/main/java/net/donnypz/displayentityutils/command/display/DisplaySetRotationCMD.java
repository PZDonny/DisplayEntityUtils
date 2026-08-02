package net.donnypz.displayentityutils.command.display;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.Axis;
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

import java.util.List;

class DisplaySetRotationCMD extends PartsSubCommand {

    DisplaySetRotationCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("setrotation", parentSubCommand, Permission.DISPLAY_TRANSFORM, true);
        setTabComplete(2, List.of("x", "y", "z"));
        setTabComplete(3, "<angle-in-degrees>");
    }

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        try {
            String axisInput = args[2];
            Axis axis = Axis.valueOf(axisInput.toUpperCase());
            float rotation = Float.parseFloat(args[3]);

            for (ActivePart selectedPart : selection.getParts()) {
                if (!selectedPart.isDisplay()) continue;
                selectedPart.setRotation(rotation, axis);
            }

            player.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage
                    .miniMessage()
                    .deserialize(String.format("<green>Rotating your selected parts on their local <yellow>%s <green>axis by <yellow>%s <green>degrees!",
                            args[2].toUpperCase(),
                            rotation))));
            return true;
        } catch (NumberFormatException e) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a valid number for the scale!", NamedTextColor.RED)));
            return false;
        }
        catch(IllegalArgumentException e){
            super.incorrectUsage(player);
            return false;
        }
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (isNotDisplay(player, selectedPart)) return false;
        try {
            String axisInput = args[2];
            Axis axis = Axis.valueOf(axisInput.toUpperCase());
            float rotation = Float.parseFloat(args[3]);

            selectedPart.setRotation(rotation, axis);

            player.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage
                    .miniMessage()
                    .deserialize(String.format("<green>Rotating your selected part on the local <yellow>%s <green>axis by <yellow>%s <green>degrees!",
                            args[2].toUpperCase(),
                            rotation))));
            return true;
        } catch (NumberFormatException e) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a valid number for the rotation!", NamedTextColor.RED)));
            return false;
        }
        catch(IllegalArgumentException e){
            super.incorrectUsage(player);
            return false;
        }
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected String getDescription() {
        return "Rotate your selected display on an axis";
    }
}
