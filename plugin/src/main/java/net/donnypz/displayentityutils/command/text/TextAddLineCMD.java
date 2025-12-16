package net.donnypz.displayentityutils.command.text;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePartSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.MultiPartSelection;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class TextAddLineCMD extends PartsSubCommand {

    public TextAddLineCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("addline", parentSubCommand, Permission.TEXT_SET_TEXT, 3, 0);
        setTabComplete(2, "<text>");
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(Component.text("Incorrect Usage! /deu text addline <text>", NamedTextColor.RED));
    }

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        return false;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        Component currentText = selectedPart.getTextDisplayText();
        Key font = currentText.font();
        Component comp = currentText.appendNewline().append(LegacyComponentSerializer.legacyAmpersand().deserialize(TextSetCMD.getTextResult(args)));
        selectedPart.setTextDisplayText(comp.font(font));
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Successfully added line to text display!", NamedTextColor.GREEN)));
        return true;
    }
}
