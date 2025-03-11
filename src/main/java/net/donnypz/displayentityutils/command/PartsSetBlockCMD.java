package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;

class PartsSetBlockCMD extends PlayerSubCommand {
    PartsSetBlockCMD() {
        super(Permission.PARTS_SET_BLOCK);
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
            player.sendMessage(Component.text("Incorrect Usage! /mdis parts setblock <\"-held\" | \"-target\" | block-id> [-all]", NamedTextColor.RED));
            return;
        }
        
        String block = args[2];

        if (partSelection.getSelectedParts().isEmpty()){
            PartsCMD.invalidPartSelection(player);
            return;
        }

        BlockData blockData = DEUCommandUtils.getBlockFromText(block, player);
        if (blockData == null) return;

        if (args.length >= 4 && args[3].equalsIgnoreCase("-all")){
            for (SpawnedDisplayEntityPart part : partSelection.getSelectedParts()){
                if (part.getType() == SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY) {
                    setBlock(part, blockData);
                }
            }
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Successfully set block of ALL selected block displays!", NamedTextColor.GREEN)));
        }
        else{
            SpawnedDisplayEntityPart selected = partSelection.getSelectedPart();
            if (selected.getType() != SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY) {
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("You can only do this with block display entities", NamedTextColor.RED)));
                return;
            }
            setBlock(selected, blockData);
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Successfully set block of selected block display!", NamedTextColor.GREEN)));
        }
    }

    private void setBlock(SpawnedDisplayEntityPart part, BlockData blockData){
        BlockDisplay display = (BlockDisplay) part.getEntity();
        display.setBlock(blockData);
    }

}
