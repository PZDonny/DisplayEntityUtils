package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class PartsAddTagCMD extends PartsSubCommand {
    PartsAddTagCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("addtag", parentSubCommand, Permission.PARTS_TAG, 3, 3);
        setTabComplete(2,"<part-tag>");
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(Component.text("Provide a part tag! /mdis parts addtag <part-tag> [-all]", NamedTextColor.RED));
    }

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        String tag  = args[2];
        if (selection.addTag(tag)){
            player.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Adding part tag to ALL selected parts! <white>(Added Tag: "+tag+")")));
            return true;
        }
        else{
            DisplayEntityPluginCommand.invalidTag(player, tag);
            return false;
        }
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        String tag  = args[2];
        if (selectedPart.addTag(tag)){
            player.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Adding part tag to selected part! <white>(Added Tag: "+tag+")")));
            return true;
        }
        else{
            DisplayEntityPluginCommand.invalidTag(player, tag);
            return false;
        }
    }
}
