package net.donnypz.displayentityutils.command.item;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ItemTransformCMD extends PartsSubCommand {
    ItemTransformCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("transform", parentSubCommand, Permission.ITEM_TRANSFORM, 3, 3);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(Component.text("Incorrect Usage! /mdis item transform <transform-type> [-all]", NamedTextColor.RED));
    }

    @Override
    protected void executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        ItemDisplay.ItemDisplayTransform transform = getTransform(player, args[2]);
        if (transform == null) return;
        for (ActivePart part : selection.getSelectedParts()){
            part.setItemDisplayTransform(transform);
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Successfully set item transform of ALL selected item displays!", NamedTextColor.GREEN)));
    }

    @Override
    protected void executeSinglePartAction(@NotNull Player player, @Nullable SpawnedDisplayEntityGroup group, @NotNull ActivePartSelection<?> selection, @NotNull SpawnedDisplayEntityPart selectedPart, @NotNull String[] args) {
        ItemDisplay.ItemDisplayTransform transform = getTransform(player, args[2]);
        if (transform == null) return;
        if (selectedPart.getType() != SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You can only do this with item display entities", NamedTextColor.RED)));
            return;
        }
        selectedPart.setItemDisplayTransform(transform);
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Successfully set item transform of selected item display!", NamedTextColor.GREEN)));
    }


    private ItemDisplay.ItemDisplayTransform getTransform(Player player, String transform){
        try{
            return ItemDisplay.ItemDisplayTransform.valueOf(transform.toUpperCase());
        }
        catch(IllegalArgumentException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid item transform option!", NamedTextColor.RED)));
            return null;
        }
    }

}
