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
        super("toggleimmovable", parentSubCommand, Permission.MANNEQUIN_IMMOVABLE, 2, 2);
        setTabComplete(3, List.of("on", "off"));
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect ALL usage! /deu mannequin toggleimmovable [-all <on | off>]", NamedTextColor.RED)));
    }

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        if (args.length < 4){
            sendIncorrectUsage(player);
            return false;
        }

        boolean status;
        String s = args[3];
        if (s.equalsIgnoreCase("on")){
            status = true;
            player.sendMessage(DisplayAPI.pluginPrefix
                    .append(MiniMessage.miniMessage().deserialize("<green>Toggled immovability for ALL selected mannequins ON")));
        }
        else if (s.equalsIgnoreCase("off")){
            status = false;
            player.sendMessage(DisplayAPI.pluginPrefix
                    .append(MiniMessage.miniMessage().deserialize("<green>Toggled immovability for ALL selected mannequins <red>OFF")));
        }
        else{
            sendIncorrectUsage(player);
            return false;
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
        isInvalidType(player, selectedPart, SpawnedDisplayEntityPart.PartType.MANNEQUIN);
        selectedPart.setMannequinImmovable(!selectedPart.isMannequinImmovable());
        String status = selectedPart.isMannequinImmovable() ? "<green>ON" : "<red>OFF";
        player.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Toggled immovability of selected mannequin "+status)));
        return true;
    }
}
