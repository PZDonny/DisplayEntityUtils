package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

class PartsSetBlockCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.PARTS_SET_BLOCK)){
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
            player.sendMessage(Component.text("Incorrect Usage! /mdis parts setblock <\"-held\" | \"-target\" | block-id>"));
            return;
        }
        
        String block = args[2];
        if (partSelection.getSelectedParts().size() > 1){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"You can only do this with one part selected");
            return;
        }
        if (partSelection.getSelectedParts().getFirst().getType() != SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"You can only do this with block display entities");
            return;
        }

        BlockDisplay display = (BlockDisplay) partSelection.getSelectedParts().getFirst().getEntity();
        BlockData blockData;

        //Held Block
        if (block.equals("-held")){
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            if (!mainHand.getType().isBlock()){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"You must be holding a block to do that!");
                return;
            }
            blockData = mainHand.getType().createBlockData();
        }

        //Target Block
        else if (block.equals("-target")){
            int targetDistance = 30;
            RayTraceResult result = player.rayTraceBlocks(targetDistance);
            Block b = null;
            if (result != null){
                b = result.getHitBlock();
            }
            if (result == null || b == null){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Block not found, target a block within "+targetDistance+" of you");
                return;
            }
            b = result.getHitBlock();
            blockData = b.getBlockData();
        }

        //Block-ID
        else{
            Material material = Material.matchMaterial(block.toLowerCase());
            if (material == null || !material.isBlock()){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Block not recognized, an item's name might have been entered or the block doesn't exist");
                return;
            }
            blockData = material.createBlockData();
        }

        display.setBlock(blockData);
        player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Successfully set block of block display!");
    }

}
