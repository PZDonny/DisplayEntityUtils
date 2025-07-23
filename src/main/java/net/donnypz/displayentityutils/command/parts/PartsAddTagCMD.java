package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.ServerSideSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class PartsAddTagCMD extends PartsSubCommand {
    PartsAddTagCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("addtag", parentSubCommand, Permission.PARTS_TAG, 3, 3);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(Component.text("Provide a part tag! /mdis parts addtag <part-tag> [-all]", NamedTextColor.RED));
    }

    @Override
    protected void executeAllPartsAction(@NotNull Player player, @Nullable SpawnedDisplayEntityGroup group, @NotNull SpawnedPartSelection selection, @NotNull String[] args) {
        String tag  = args[2];
        if (selection.addTag(tag)){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Adding part tag to ALL selected parts! <white>(Added Tag: "+tag+")")));
        }
        else{
            DisplayEntityPluginCommand.invalidTag(player, tag);
        }
    }

    @Override
    protected void executeSinglePartAction(@NotNull Player player, @Nullable SpawnedDisplayEntityGroup group, @NotNull ServerSideSelection selection, @NotNull SpawnedDisplayEntityPart selectedPart, @NotNull String[] args) {
        String tag  = args[2];
        if (selectedPart.addTag(tag)){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Adding part tag to selected part! <white>(Added Tag: "+tag+")")));
        }
        else{
            DisplayEntityPluginCommand.invalidTag(player, tag);
        }
    }
}
