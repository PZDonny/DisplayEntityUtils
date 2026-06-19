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
        super("toggleglint", parentSubCommand, Permission.ITEM_TOGGLE_GLINT, false);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {

        boolean status;
        OptionalArguments oArgs = getOptionalArguments(player, args);
        if (oArgs.getOption("-all").equals("on")){
            status = true;
            player.sendMessage(DisplayAPI.pluginPrefix
                    .append(MiniMessage.miniMessage().deserialize("<green>Toggled glint for ALL selected item displays ON")));
        }
        else{
            status = false;
            player.sendMessage(DisplayAPI.pluginPrefix
                    .append(MiniMessage.miniMessage().deserialize("<green>Toggled glint for ALL selected item displays <red>OFF")));
        }

        for (ActivePart part : selection.getSelectedParts()){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY) {
                part.setItemDisplayItemGlint(status);
            }
        }
        return true;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (isInvalidType(player, selectedPart, SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY)) return false;

        ItemStack item = selectedPart.getItemDisplayItem();
        if (item == null) return false;
        selectedPart.setItemDisplayItemGlint(!selectedPart.hasItemDisplayItemGlint());
        String status = selectedPart.hasItemDisplayItemGlint() ? "<green>ON" : "<red>OFF";
        player.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Toggled glint of selected item display "+status)));
        return true;
    }

    @Override
    protected String getDescription() {
        return "Toggle the enchantment glint of an item display's item";
    }
}
