package net.donnypz.displayentityutils.command.mannequin;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.PartsSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

class MannequinHeldItemCMD extends PartsSubCommand {
    MannequinHeldItemCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("helditem", parentSubCommand, Permission.MANNEQUIN_SET_EQUIPMENT,4, 4);
        setTabComplete(2, List.of("main", "off"));
        setTabComplete(3, List.of("-held", "<item-id>"));
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(Component.text("Incorrect Usage! /deu mannequin helditem <main | off> <\"-held\" | item-id> [-all]", NamedTextColor.RED));
    }

    @Override
    protected boolean executeAllPartsAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull MultiPartSelection<?> selection, @NotNull String[] args) {
        String hand = args[2];
        boolean isMainHand;
        if (hand.equalsIgnoreCase("main")){
            isMainHand = true;
        }
        else if (hand.equalsIgnoreCase("off") || hand.equalsIgnoreCase("offhand")) {
            isMainHand = false;
        }
        else{
            sendIncorrectUsage(player);
            return false;
        }

        String item = args[3];
        ItemStack itemStack = DEUCommandUtils.getItemFromText(item, player);
        if (itemStack == null) return false;

        for (ActivePart part : selection.getSelectedParts()){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.MANNEQUIN) {
                part.setMannequinHandItem(itemStack, isMainHand);
            }
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Set "+hand.toLowerCase()+" hand item of ALL selected mannequins!", NamedTextColor.GREEN)));
        return true;
    }

    @Override
    protected boolean executeSinglePartAction(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull ActivePartSelection<?> selection, @NotNull ActivePart selectedPart, @NotNull String[] args) {
        if (isInvalidType(player, selectedPart, SpawnedDisplayEntityPart.PartType.MANNEQUIN)) return false;
        String hand = args[2];
        boolean isMainHand;
        if (hand.equalsIgnoreCase("main")){
            isMainHand = true;
        }
        else if (hand.equalsIgnoreCase("off") || hand.equalsIgnoreCase("offhand")) {
            isMainHand = false;
        }
        else{
            sendIncorrectUsage(player);
            return false;
        }

        String item = args[3];
        ItemStack itemStack = DEUCommandUtils.getItemFromText(item, player);
        if (itemStack == null) return false;

        selectedPart.setMannequinHandItem(itemStack, isMainHand);
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Set "+hand.toLowerCase()+" hand item of selected mannequin!", NamedTextColor.GREEN)));
        return true;
    }
}
