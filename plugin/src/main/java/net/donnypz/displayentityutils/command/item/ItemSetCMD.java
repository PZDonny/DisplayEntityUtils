package net.donnypz.displayentityutils.command.item;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

class ItemSetCMD extends PartsSubCommand {
    ItemSetCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("set", parentSubCommand, Permission.ITEM_SET,3, 3);
        setTabComplete(2, List.of("-held", "<item-id>"));
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(Component.text("Incorrect Usage! /mdis parts setitem <\"-held\" | item-id> [-all]", NamedTextColor.RED));
    }

    @Override
    protected void executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        String item = args[2];
        ItemStack itemStack = DEUCommandUtils.getItemFromText(item, player);
        if (itemStack == null) return;

        for (ActivePart part : selection.getSelectedParts()){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY) {
                part.setItemDisplayItem(itemStack);
            }
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Successfully set item of ALL selected item displays!", NamedTextColor.GREEN)));
    }

    @Override
    protected void executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        String item = args[2];
        ItemStack itemStack = DEUCommandUtils.getItemFromText(item, player);
        if (itemStack == null) return;

        if (selectedPart.getType() != SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You can only do this with item display entities", NamedTextColor.RED)));
            return;
        }
        selectedPart.setItemDisplayItem(itemStack);
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Successfully set item of selected item display!", NamedTextColor.GREEN)));
    }



}
