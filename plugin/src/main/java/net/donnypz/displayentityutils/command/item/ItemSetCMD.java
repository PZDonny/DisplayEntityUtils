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
        player.sendMessage(Component.text("Incorrect Usage! /deu parts setitem <\"-held\" | item-id> [-all]", NamedTextColor.RED));
    }

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        String item = args[2];
        ItemStack itemStack = DEUCommandUtils.getItemFromText(item, player);
        if (itemStack == null) return false;

        for (ActivePart part : selection.getSelectedParts()){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY) {
                part.setItemDisplayItem(itemStack);
            }
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Set item of ALL selected item displays!", NamedTextColor.GREEN)));
        return true;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        String item = args[2];
        ItemStack itemStack = DEUCommandUtils.getItemFromText(item, player);
        if (itemStack == null) return false;

        if (selectedPart.getType() != SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You can only do this with item display entities", NamedTextColor.RED)));
            return false;
        }
        selectedPart.setItemDisplayItem(itemStack);
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Set item of selected item display!", NamedTextColor.GREEN)));
        return true;
    }
}
