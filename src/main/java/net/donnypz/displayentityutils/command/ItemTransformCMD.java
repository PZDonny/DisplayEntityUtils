package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;

class ItemTransformCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.ITEM_TRANSFORM)){
            return;
        }

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
            player.sendMessage(Component.text("Incorrect Usage! /mdis item transform <transform-type> [-all]", NamedTextColor.RED));
            return;
        }


        if (partSelection.getSelectedParts().isEmpty()){
            PartsCMD.invalidPartSelection(player);
            return;
        }

        try{
            ItemDisplay.ItemDisplayTransform transform = ItemDisplay.ItemDisplayTransform.valueOf(args[2].toUpperCase());

            if (args.length >= 4 && args[3].equalsIgnoreCase("-all")){
                for (SpawnedDisplayEntityPart part : partSelection.getSelectedParts()){
                    if (part.getType() == SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY) {
                        setTransform(part, transform);
                    }
                }
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Successfully set item transform of ALL selected item displays!", NamedTextColor.GREEN)));
            }
            else{
                SpawnedDisplayEntityPart selected = partSelection.getSelectedPart();
                if (selected.getType() != SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY) {
                    player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("You can only do this with item display entities", NamedTextColor.RED)));
                    return;
                }
                setTransform(selected, transform);
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Successfully set item transform of selected item display!", NamedTextColor.GREEN)));
            }
        }
        catch(IllegalArgumentException e){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Invalid item transform option!", NamedTextColor.RED)));
        }

    }

    private void setTransform(SpawnedDisplayEntityPart part, ItemDisplay.ItemDisplayTransform transform){
        ItemDisplay display = (ItemDisplay) part.getEntity();
        display.setItemDisplayTransform(transform);
    }

}
