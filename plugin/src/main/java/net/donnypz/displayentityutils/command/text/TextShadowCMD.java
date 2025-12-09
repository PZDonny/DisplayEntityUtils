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

class TextShadowCMD extends PartsSubCommand {
    TextShadowCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("shadow", parentSubCommand, Permission.TEXT_TOGGLE_SHADOW, 0, 2);
        setTabComplete(3, List.of("on", "off"));
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect ALL usage! /deu text shadow -all <on | off>", NamedTextColor.RED)));
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
                    .append(MiniMessage.miniMessage().deserialize("<green>Successfully toggled shadowed for ALL selected text displays ON")));
        }
        else if (s.equalsIgnoreCase("off")){
            status = false;
            player.sendMessage(DisplayAPI.pluginPrefix
                    .append(MiniMessage.miniMessage().deserialize("<green>Successfully toggled shadowed for ALL selected text displays <red>OFF")));
        }
        else{
            sendIncorrectUsage(player);
            return false;
        }

        for (ActivePart part : selection.getSelectedParts()){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
                part.setTextDisplayShadowed(status);
            }
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Successfully toggled shadowed for ALL selected text displays!", NamedTextColor.GREEN)));
        return true;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (selectedPart.getType() != SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You can only do this with text display entities", NamedTextColor.RED)));
            return false;
        }
        selectedPart.setTextDisplayShadowed(!selectedPart.isTextDisplayShadowed());
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Successfully toggled shadow for text display!", NamedTextColor.GREEN)));
        return true;
    }
}
