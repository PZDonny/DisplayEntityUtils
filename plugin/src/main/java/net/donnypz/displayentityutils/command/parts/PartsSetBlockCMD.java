package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.DisplayEntities.ServerSideSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class PartsSetBlockCMD extends PartsSubCommand {
    PartsSetBlockCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("setblock", parentSubCommand, Permission.PARTS_SET_BLOCK, 3, 3);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(Component.text("Incorrect Usage! /mdis parts setblock <\"-held\" | \"-target\" | block-id> [-all]", NamedTextColor.RED));
    }

    @Override
    protected void executeAllPartsAction(@NotNull Player player, @Nullable SpawnedDisplayEntityGroup group, @NotNull SpawnedPartSelection selection, @NotNull String[] args) {
        BlockData blockData = DEUCommandUtils.getBlockFromText(args[2], player);
        if (blockData == null) return;
        for (SpawnedDisplayEntityPart part : selection.getSelectedParts()){
            if (part.isMaster()) continue;
            if (part.getType() == SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY) {
                setBlock(part, blockData);
            }
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Successfully set block of ALL selected block displays!", NamedTextColor.GREEN)));
    }

    @Override
    protected void executeSinglePartAction(@NotNull Player player, @Nullable SpawnedDisplayEntityGroup group, @NotNull ServerSideSelection selection, @NotNull SpawnedDisplayEntityPart selectedPart, @NotNull String[] args) {
        BlockData blockData = DEUCommandUtils.getBlockFromText(args[2], player);
        if (blockData == null) return;
        if (selectedPart.getType() != SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You can only do this with block display entities", NamedTextColor.RED)));
            return;
        }

        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Successfully set block of selected block display!", NamedTextColor.GREEN)));
        setBlock(selectedPart, blockData);
    }

    private void setBlock(SpawnedDisplayEntityPart part, BlockData blockData){
        BlockDisplay display = (BlockDisplay) part.getEntity();
        display.setBlock(blockData);
    }
}
