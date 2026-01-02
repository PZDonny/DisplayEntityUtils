package net.donnypz.displayentityutils.command.display;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

class DisplaySetBlockCMD extends PartsSubCommand {
    DisplaySetBlockCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("setblock", parentSubCommand, Permission.DISPLAY_SET_BLOCK, 3, 3);
        setTabComplete(2, List.of("-held", "-target", "<block-id>"));
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(Component.text("Incorrect Usage! /deu display setblock <\"-held\" | \"-target\" | block-id> [-all]", NamedTextColor.RED));
    }

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        BlockData blockData = DEUCommandUtils.getBlockFromText(args[2], player);
        if (blockData == null) return false;
        for (ActivePart part : selection.getSelectedParts()){
            if (part.isMaster()) continue;
            if (part.getType() == SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY) {
                setBlock(part, blockData);
            }
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Set block of ALL selected block displays!", NamedTextColor.GREEN)));
        return true;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (isInvalidType(player, selectedPart, SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY)) return false;
        BlockData blockData = DEUCommandUtils.getBlockFromText(args[2], player);
        if (blockData == null) return false;

        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Set block of selected block display!", NamedTextColor.GREEN)));
        setBlock(selectedPart, blockData);
        return true;
    }

    private void setBlock(ActivePart part, BlockData blockData){
        part.setBlockDisplayBlock(blockData);
    }
}
