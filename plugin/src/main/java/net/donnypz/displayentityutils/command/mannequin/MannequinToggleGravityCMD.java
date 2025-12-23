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
        super("togglegravity", parentSubCommand, Permission.MANNEQUIN_GRAVITY, 2, 2);
        setTabComplete(3, List.of("on", "off"));
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect ALL usage! /deu mannequin togglegravity [-all <on | off>]", NamedTextColor.RED)));
    }

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        if (args.length < 4){
            sendIncorrectUsage(player);
            return false;
        }

        if (group != null){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You cannot change the gravity of mannequins in a group!", NamedTextColor.RED)));
            return false;
        }

        boolean status;
        String s = args[3];
        if (s.equalsIgnoreCase("on")){
            status = true;
            player.sendMessage(DisplayAPI.pluginPrefix
                    .append(MiniMessage.miniMessage().deserialize("<green>Toggled gravity for ALL selected mannequins ON")));
        }
        else if (s.equalsIgnoreCase("off")){
            status = false;
            player.sendMessage(DisplayAPI.pluginPrefix
                    .append(MiniMessage.miniMessage().deserialize("<green>Toggled gravity for ALL selected mannequins <red>OFF")));
        }
        else{
            sendIncorrectUsage(player);
            return false;
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
}
