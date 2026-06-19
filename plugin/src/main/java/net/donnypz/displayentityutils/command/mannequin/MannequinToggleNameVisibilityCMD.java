package net.donnypz.displayentityutils.command.mannequin;

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

class MannequinToggleNameVisibilityCMD extends PartsSubCommand {
    MannequinToggleNameVisibilityCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("togglenamevisibility", parentSubCommand, Permission.MANNEQUIN_NAME_VISIBLE, false);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        if (args.length < 4){
            sendIncorrectUsage(player);
            return false;
        }

        boolean status;
        OptionalArguments oArgs = getOptionalArguments(player, args);
        if (oArgs.getOption("-all").equals("on")){
            status = true;
            player.sendMessage(DisplayAPI.pluginPrefix
                    .append(MiniMessage.miniMessage().deserialize("<green>Toggled name visibility for ALL selected mannequins ON")));
        }
        else {
            status = false;
            player.sendMessage(DisplayAPI.pluginPrefix
                    .append(MiniMessage.miniMessage().deserialize("<green>Toggled name visibility for ALL selected mannequins <red>OFF")));
        }

        for (ActivePart part : selection.getSelectedParts()){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.MANNEQUIN) {
                part.setCustomNameVisible(status);
            }
        }
        return true;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (isInvalidType(player, selectedPart, SpawnedDisplayEntityPart.PartType.MANNEQUIN)) return false;
        selectedPart.setCustomNameVisible(!selectedPart.isCustomNameVisible());
        String status = selectedPart.isCustomNameVisible() ? "<green>ON" : "<red>OFF";
        player.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Toggled name visibility of selected mannequin "+status)));
        return true;
    }
}
