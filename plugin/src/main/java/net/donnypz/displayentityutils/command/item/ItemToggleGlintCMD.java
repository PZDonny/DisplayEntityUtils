package net.donnypz.displayentityutils.command.item;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

class ItemToggleGlintCMD extends PartsSubCommand {
    ItemToggleGlintCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("toggleglint", parentSubCommand, Permission.ITEM_TOGGLE_GLINT, 2, 2);
        setTabComplete(3, List.of("on", "off"));
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Incorrect ALL usage! /mdis item toggleglint -all <on | off>", NamedTextColor.RED)));
    }

    @Override
    protected void executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        if (args.length < 4){
            sendIncorrectUsage(player);
            return;
        }

        boolean status;
        String s = args[3];
        if (s.equalsIgnoreCase("on")){
            status = true;
            player.sendMessage(DisplayAPI.pluginPrefix
                    .append(MiniMessage.miniMessage().deserialize("<green>Successfully toggled glint for ALL selected item displays ON")));
        }
        else if (s.equalsIgnoreCase("off")){
            status = false;
            player.sendMessage(DisplayAPI.pluginPrefix
                    .append(MiniMessage.miniMessage().deserialize("<green>Successfully toggled glint for ALL selected item displays <red>OFF")));
        }
        else{
            sendIncorrectUsage(player);
            return;
        }

        for (ActivePart part : selection.getSelectedParts()){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY) {
                part.setItemDisplayItemGlint(status);
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
        ItemStack item = selectedPart.getItemDisplayItem();
        if (item == null) return;
        selectedPart.setItemDisplayItemGlint(!item.getItemMeta().getEnchantmentGlintOverride());
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Successfully toggled glint of selected item display!", NamedTextColor.GREEN)));
    }
}
