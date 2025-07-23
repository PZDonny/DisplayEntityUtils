package net.donnypz.displayentityutils.command.item;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.ServerSideSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
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
    protected void executeAllPartsAction(@NotNull Player player, @Nullable SpawnedDisplayEntityGroup group, @NotNull SpawnedPartSelection selection, @NotNull String[] args) {
        ItemDisplay.ItemDisplayTransform transform = getTransform(player, args[2]);
        if (transform == null) return;
        for (SpawnedDisplayEntityPart part : selection.getSelectedParts()){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY) {
                setTransform(part, transform);
            }
        }
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Successfully set item transform of ALL selected item displays!", NamedTextColor.GREEN)));
    }

    @Override
    protected void executeSinglePartAction(@NotNull Player player, @Nullable SpawnedDisplayEntityGroup group, @NotNull ServerSideSelection selection, @NotNull SpawnedDisplayEntityPart selectedPart, @NotNull String[] args) {
        ItemDisplay.ItemDisplayTransform transform = getTransform(player, args[2]);
        if (transform == null) return;
        if (selectedPart.getType() != SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("You can only do this with item display entities", NamedTextColor.RED)));
            return;
        }
        setTransform(selectedPart, transform);
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Successfully set item transform of selected item display!", NamedTextColor.GREEN)));
    }

    private void setTransform(SpawnedDisplayEntityPart part, ItemDisplay.ItemDisplayTransform transform){
        ItemDisplay display = (ItemDisplay) part.getEntity();
        display.setItemDisplayTransform(transform);
    }

    private ItemDisplay.ItemDisplayTransform getTransform(Player player, String transform){
        try{
            return ItemDisplay.ItemDisplayTransform.valueOf(transform.toUpperCase());
        }
        catch(IllegalArgumentException e){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Invalid item transform option!", NamedTextColor.RED)));
            return null;
        }
    }

}
