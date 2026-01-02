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
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

class DisplayResetTranslationCMD extends PartsSubCommand {
    DisplayResetTranslationCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("resettranslation", parentSubCommand, Permission.DISPLAY_TRANSLATE, 2, 2);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        for (ActivePart p : selection.getSelectedParts()){
            if (!p.isDisplay()) continue;
            Vector3f translation = p.getTransformation().getTranslation();
            p.translate(Vector.fromJOML(translation), translation.length(), 0, 0);
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Translation reset for all selected displays!", NamedTextColor.GREEN)));
        return true;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (!DisplayCMD.isDisplay(player, selectedPart)) return false;
        Vector3f translation = selectedPart.getTransformation().getTranslation().negate();
        selectedPart.translate(Vector.fromJOML(translation), translation.length(), 0, 0);
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Reset selected display part's translation!", NamedTextColor.GREEN)));
        return true;
    }
}
