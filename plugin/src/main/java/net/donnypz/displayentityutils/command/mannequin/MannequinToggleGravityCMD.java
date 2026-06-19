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

class MannequinToggleGravityCMD extends PartsSubCommand {
    MannequinToggleGravityCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("togglegravity", parentSubCommand, Permission.MANNEQUIN_GRAVITY, false);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        if (group != null){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You cannot change the gravity of mannequins in a group!", NamedTextColor.RED)));
            return false;
        }

        boolean status;
        OptionalArguments oArgs = getOptionalArguments(player, args);
        if (oArgs.getOption("-all").equals("on")){
            status = true;
            player.sendMessage(DisplayAPI.pluginPrefix
                    .append(MiniMessage.miniMessage().deserialize("<green>Toggled gravity for ALL selected mannequins ON")));
        }
        else {
            status = false;
            player.sendMessage(DisplayAPI.pluginPrefix
                    .append(MiniMessage.miniMessage().deserialize("<green>Toggled gravity for ALL selected mannequins <red>OFF")));
        }

        for (ActivePart part : selection.getSelectedParts()){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.MANNEQUIN) {
                part.setMannequinGravity(status);
            }
        }
        return true;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (isInvalidType(player, selectedPart, SpawnedDisplayEntityPart.PartType.MANNEQUIN)) return false;
        if (group != null){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You cannot change the gravity of a mannequin in a group!", NamedTextColor.RED)));
            return false;
        }
        selectedPart.setMannequinGravity(!selectedPart.hasMannequinGravity());
        String status = selectedPart.hasMannequinGravity() ? "<green>ON" : "<red>OFF";
        player.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Toggled gravity of selected mannequin "+status)));
        return true;
    }

    @Override
    protected String getDescription() {
        return "Toggle the gravity of an mannequin";
    }
}
