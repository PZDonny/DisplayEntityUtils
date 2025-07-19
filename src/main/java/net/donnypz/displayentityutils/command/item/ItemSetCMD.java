package net.donnypz.displayentityutils.command.item;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.parts.PartsCMD;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

class ItemSetCMD extends PlayerSubCommand {
    ItemSetCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("set", parentSubCommand, Permission.ITEM_SET);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(player);
        if (partSelection == null){
            PartsCMD.noPartSelection(player);
            return;
        }

        if (args.length < 3){
            player.sendMessage(Component.text("Incorrect Usage! /mdis parts setitem <\"-held\" | item-id> [-all]", NamedTextColor.RED));
            return;
        }
        
        String item = args[2];

        if (partSelection.getSelectedParts().isEmpty()){
            PartsCMD.invalidPartSelection(player);
            return;
        }


        ItemStack itemStack = DEUCommandUtils.getItemFromText(item, player);
        if (itemStack == null) return;

        if (args.length >= 4 && args[3].equalsIgnoreCase("-all")){
            for (SpawnedDisplayEntityPart part : partSelection.getSelectedParts()){
                if (part.getType() == SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY) {
                    setItem(part, itemStack);
                }
            }
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Successfully set item of ALL selected item displays!", NamedTextColor.GREEN)));
        }
        else{
            SpawnedDisplayEntityPart selected = partSelection.getSelectedPart();
            if (selected.getType() != SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY) {
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("You can only do this with item display entities", NamedTextColor.RED)));
                return;
            }
            setItem(selected, itemStack);
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Successfully set item of selected item display!", NamedTextColor.GREEN)));
        }
    }

    private void setItem(SpawnedDisplayEntityPart part, ItemStack itemStack){
        ItemDisplay display = (ItemDisplay) part.getEntity();
        display.setItemStack(itemStack);
    }

}
