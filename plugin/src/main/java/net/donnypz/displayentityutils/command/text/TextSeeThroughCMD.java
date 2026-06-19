package net.donnypz.displayentityutils.command.text;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

class TextSeeThroughCMD extends PartsSubCommand {
    TextSeeThroughCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("seethrough", parentSubCommand, Permission.TEXT_TOGGLE_SEE_THROUGH, false);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {

        boolean status;
        OptionalArguments oArgs = getOptionalArguments(player, args);
        if (oArgs.getOption("-all").equals("on")){
            status = true;
            player.sendMessage(DisplayAPI.pluginPrefix
                    .append(MiniMessage.miniMessage().deserialize("<green>Toggled see through for ALL selected text displays ON")));
        }
        else {
            status = false;
            player.sendMessage(DisplayAPI.pluginPrefix
                    .append(MiniMessage.miniMessage().deserialize("<green>Toggled see through for ALL selected text displays <red>OFF")));
        }

        for (ActivePart part : selection.getSelectedParts()){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
                part.setTextDisplaySeeThrough(status);
            }
        }
        return true;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (isInvalidType(player, selectedPart, SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY)) return false;

        selectedPart.setTextDisplaySeeThrough(!selectedPart.isTextDisplaySeeThrough());
        String status = selectedPart.isTextDisplaySeeThrough() ? "<green>ON" : "<red>OFF";
        player.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Toggled see through for your selected text display "+status)));
        return true;
    }
}
