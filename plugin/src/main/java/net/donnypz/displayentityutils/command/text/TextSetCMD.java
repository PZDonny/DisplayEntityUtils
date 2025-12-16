package net.donnypz.displayentityutils.command.text;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class TextSetCMD extends PartsSubCommand {

    public TextSetCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("set", parentSubCommand, Permission.TEXT_SET_TEXT, 3, 0);
        setTabComplete(2, "<text>");
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(Component.text("Incorrect Usage! /deu text set <text>", NamedTextColor.RED));
    }

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        return false;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (selectedPart.getType() != SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You can only do this with text display entities", NamedTextColor.RED)));
            return false;
        }
        Component currentText = selectedPart.getTextDisplayText();
        Key font = currentText.font();
        Component comp = LegacyComponentSerializer.legacyAmpersand().deserialize(getTextResult(args));
        selectedPart.setTextDisplayText(comp.font(font));
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Successfully set text on text display!", NamedTextColor.GREEN)));
        player.sendMessage(Component.text("You can include \"\\n\" in your to create a new line.", NamedTextColor.GRAY));
        return true;
    }

    static String getTextResult(String[] args){
        StringBuilder builder = new StringBuilder();
        for (int i = 2; i < args.length; i++){
            builder.append(args[i]);
            if (i+1 != args.length){
                builder.append(" ");
            }
        }
        return builder.toString().replace("\\n", "\n");
    }
}
