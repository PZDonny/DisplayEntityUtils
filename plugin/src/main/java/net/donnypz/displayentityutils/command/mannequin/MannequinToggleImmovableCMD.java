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

class MannequinToggleImmovableCMD extends PartsSubCommand {
    MannequinToggleImmovableCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("toggleimmovable", parentSubCommand, Permission.MANNEQUIN_IMMOVABLE, false);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {

        if (group != null){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You cannot change the immovability of mannequins in a group!", NamedTextColor.RED)));
            return false;
        }

        boolean status;
        OptionalArguments oArgs = getOptionalArguments(player, args);
        if (!oArgs.isValidOptions()) return false;
        if (oArgs.getOption("-all").equals("on")){
            status = true;
            player.sendMessage(DisplayAPI.pluginPrefix
                    .append(MiniMessage.miniMessage().deserialize("<green>Toggled immovability for ALL selected mannequins ON")));
        }
        else {
            status = false;
            player.sendMessage(DisplayAPI.pluginPrefix
                    .append(MiniMessage.miniMessage().deserialize("<green>Toggled immovability for ALL selected mannequins <red>OFF")));
        }

        for (ActivePart part : selection.getSelectedParts()){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.MANNEQUIN) {
                part.setMannequinImmovable(status);
            }
        }
        return true;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (isInvalidType(player, selectedPart, SpawnedDisplayEntityPart.PartType.MANNEQUIN)) return false;

        if (group != null){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You cannot change the immovability of a mannequin in a group!", NamedTextColor.RED)));
            return false;
        }
        selectedPart.setMannequinImmovable(!selectedPart.isMannequinImmovable());
        String status = selectedPart.isMannequinImmovable() ? "<green>ON" : "<red>OFF";
        player.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Toggled immovability of selected mannequin "+status)));
        return true;
    }

    @Override
    protected String getDescription() {
        return "Toggle the immovability of an mannequin";
    }
}
