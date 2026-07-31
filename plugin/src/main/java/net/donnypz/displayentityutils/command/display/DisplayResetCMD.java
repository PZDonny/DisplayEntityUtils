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
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

class DisplayResetCMD extends PartsSubCommand {
    DisplayResetCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("reset", parentSubCommand, Permission.DISPLAY_TRANSFORM, true);
        setTabComplete(2, List.of("translation", "rotation", "scale", "all"));
        super.cancelIfDraggingGizmo();
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        String option = args[2];
        List<? extends ActivePart> parts = selection.getParts();
        if (option.equalsIgnoreCase("translation")){
            parts.forEach(this::resetTranslation);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Translation reset for all selected displays!", NamedTextColor.GREEN)));
        }
        else if (option.equalsIgnoreCase("rotation")){
            parts.forEach(this::resetRotation);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Rotation reset for all selected displays!", NamedTextColor.GREEN)));
        }
        else if (option.equalsIgnoreCase("scale")){
            parts.forEach(this::resetScale);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Scale reset for all selected displays!", NamedTextColor.GREEN)));
        }
        else if (option.equalsIgnoreCase("all")){
            parts.forEach(this::resetAll);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Entire Transformation reset for all selected displays!", NamedTextColor.GREEN)));
        }
        return true;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (isNotDisplay(player, selectedPart)) return false;

        String option = args[2];
        if (option.equalsIgnoreCase("translation")){
            resetTranslation(selectedPart);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Reset selected display's translation!", NamedTextColor.GREEN)));
        }
        else if (option.equalsIgnoreCase("rotation")){
            resetRotation(selectedPart);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Reset selected display's rotation!", NamedTextColor.GREEN)));
        }
        else if (option.equalsIgnoreCase("scale")){
            resetScale(selectedPart);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Reset selected display's scale!", NamedTextColor.GREEN)));
        }
        else if (option.equalsIgnoreCase("all")){
            resetAll(selectedPart);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Reset selected display's entire transformation!", NamedTextColor.GREEN)));
        }
        return true;
    }

    void resetTranslation(ActivePart part){
        if (!part.isDisplay()) return;
        Vector3f translation = part.getTransformation().getTranslation().negate();
        part.translate(Vector.fromJOML(translation), translation.length(), 0, 0);
    }

    void resetRotation(ActivePart part){
        if (!part.isDisplay()) return;
        Transformation t = part.getTransformation();
        part.setTransformation(new Transformation(
                t.getTranslation(),
                new Quaternionf(),
                t.getScale(),
                new Quaternionf()
        ));
    }

    void resetScale(ActivePart part){
        if (!part.isDisplay()) return;
        part.setDisplayScale(1, 1, 1);
    }

    void resetAll(ActivePart part){
        if (!part.isDisplay()) return;
        part.setTransformation(new Transformation(
                new Vector3f(),
                new Quaternionf(),
                new Vector3f(1, 1, 1),
                new Quaternionf()
        ));
    }

    @Override
    protected String getDescription() {
        return "Reset the translation, rotation, or scale of your selected display";
    }
}
