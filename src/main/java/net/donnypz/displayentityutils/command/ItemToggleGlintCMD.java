package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

class ItemToggleGlintCMD implements PlayerSubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.ITEM_TOGGLE_GLINT)){
            return;
        }

        SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(player);
        if (partSelection == null){
            DisplayEntityPluginCommand.noPartSelection(player);
            return;
        }

        if (partSelection.getSelectedParts().isEmpty()){
            PartsCMD.invalidPartSelection(player);
            return;
        }


        if (args.length >= 4 && args[3].equalsIgnoreCase("-all")){
            for (SpawnedDisplayEntityPart part : partSelection.getSelectedParts()){
                if (part.getType() == SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY) {
                    setGlint(part);
                }
            }
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Successfully toggled glint of ALL selected item displays!", NamedTextColor.GREEN)));
        }
        else{
            SpawnedDisplayEntityPart selected = partSelection.getSelectedPart();
            if (selected.getType() != SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY) {
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("You can only do this with item display entities", NamedTextColor.RED)));
                return;
            }
            setGlint(selected);
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Successfully toggled glint of selected item display!", NamedTextColor.GREEN)));
        }
    }

    private void setGlint(SpawnedDisplayEntityPart part){
        ItemDisplay display = (ItemDisplay) part.getEntity();
        ItemStack item = display.getItemStack();
        item.editMeta(meta -> {
            if (!meta.hasEnchantmentGlintOverride()){
                meta.setEnchantmentGlintOverride(true);
            }
            else{
                meta.setEnchantmentGlintOverride(!meta.getEnchantmentGlintOverride());
            }
        });
        display.setItemStack(item);
    }
}
