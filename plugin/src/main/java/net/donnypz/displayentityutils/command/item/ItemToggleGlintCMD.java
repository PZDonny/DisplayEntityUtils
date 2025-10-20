package net.donnypz.displayentityutils.command.item;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ItemToggleGlintCMD extends PartsSubCommand {
    ItemToggleGlintCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("toggleglint", parentSubCommand, Permission.ITEM_TOGGLE_GLINT, 2, 2);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected void executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        for (ActivePart part : selection.getSelectedParts()){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY) {
                setGlint(part);
            }
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Successfully toggled glint of ALL selected item displays!", NamedTextColor.GREEN)));
    }

    @Override
    protected void executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (selectedPart.getType() != SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You can only do this with item display entities", NamedTextColor.RED)));
            return;
        }
        setGlint(selectedPart);
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Successfully toggled glint of selected item display!", NamedTextColor.GREEN)));
    }

    private void setGlint(ActivePart part){
        ItemStack item = part.getItemDisplayItem();
        if (item == null) return;
        part.setItemDisplayItemGlint(!item.getItemMeta().getEnchantmentGlintOverride());
    }
}
