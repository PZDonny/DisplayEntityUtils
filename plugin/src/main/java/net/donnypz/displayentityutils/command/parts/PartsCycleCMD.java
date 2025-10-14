package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.PacketUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

class PartsCycleCMD extends PlayerSubCommand {
    PartsCycleCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("cycle", parentSubCommand, Permission.PARTS_CYCLE);
    }

    @Override
    public void execute(Player player, String[] args) {
        ActiveGroup<?> group = DisplayGroupManager.getSelectedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        if (args.length < 3){
            player.sendMessage(Component.text("/mdis parts cycle <first | prev | next | last> [jump]", NamedTextColor.GRAY));
            return;
        }

        ActivePartSelection<?> sel = DisplayGroupManager.getPartSelection(player);
        MultiPartSelection<?> partSelection;
        if (sel == null){
            partSelection = group.createPartSelection();
            DisplayGroupManager.setPartSelection(player, partSelection, false);
        }
        else if (PartsCMD.isUnwantedSingleSelection(player, sel)){
            return;
        }
        else{
            partSelection = (MultiPartSelection<?>) sel;
        }

        int jump;
        if (args.length > 3){
            try{
                jump = Integer.parseInt(args[3]);
                if (jump <= 0){
                    throw new NumberFormatException();
                }
            }
            catch(NumberFormatException e){
                player.sendMessage(Component.text("Invalid Number! Enter a positive whole number > 0."));
                return;
            }
        }
        else{
            jump = 1;
        }

        switch(args[2]){
            case "first" -> {
                partSelection.setToFirstPart();
                displayPartInfo(player, partSelection.getSelectedPart(), partSelection);
            }
            case "last" -> {
                partSelection.setToLastPart();
                displayPartInfo(player, partSelection.getSelectedPart(), partSelection);
            }
            case "prev", "previous" -> {
                partSelection.setToPreviousPart(jump);
                displayPartInfo(player, partSelection.getSelectedPart(), partSelection);
            }
            case "next" -> {
                partSelection.setToNextPart(jump);
                displayPartInfo(player, partSelection.getSelectedPart(), partSelection);
            }
            default ->{
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid Option! /mdis parts cycle <first | prev | next | last>", NamedTextColor.RED)));
            }
        }
    }

    static Component getPartInfo(@NotNull ActivePart part){
        Component desc;
        switch(part.getType()){
            case INTERACTION -> {
                desc = MiniMessage.miniMessage().deserialize("<dark_aqua>(Interaction, H: "+part.getInteractionHeight()+" W:"+part.getInteractionWidth()+")");
            }
            case TEXT_DISPLAY -> {
                Component comp = part.getTextDisplayText();
                if (comp == null){
                    comp = Component.text("Failed to get text...", NamedTextColor.GRAY, TextDecoration.ITALIC);
                }
                desc = Component.text("(Text Display: \"", NamedTextColor.LIGHT_PURPLE)
                        .append(comp)
                        .append(Component.text("\")", NamedTextColor.LIGHT_PURPLE));
            }
            case BLOCK_DISPLAY -> {
                if (part.isMaster()){
                    desc = Component.text("(Master Entity/Part)", NamedTextColor.GOLD);
                }
                else if (part.getBlockDisplayBlock().getMaterial() == Material.AIR){
                    desc = Component.text("(Invisible Block Display | AIR, CAVE_AIR, or VOID_AIR)", NamedTextColor.GRAY);
                }
                else{
                    desc = Component.text("("+part.getBlockDisplayBlock().getMaterial().key().value()+")", NamedTextColor.YELLOW);
                }
            }
            case ITEM_DISPLAY -> {
                ItemStack i = part.getItemDisplayItem();
                if (i.getType() == Material.AIR){
                    desc = Component.text("(Invisible Item Display | AIR, CAVE_AIR, or VOID_AIR)", NamedTextColor.GRAY);
                }
                else{
                    desc = Component.text("("+i.getType().key().value()+")", NamedTextColor.AQUA);
                }
            }
            default -> throw new IllegalStateException("Unexpected part type");
        }
        return desc;
    }

    static Component getPartInfo(@NotNull Entity entity){
        Component desc;
        switch(entity){
            case Interaction i -> {
                desc = MiniMessage.miniMessage().deserialize("<dark_aqua>(Interaction, H: "+i.getInteractionHeight()+" W:"+i.getInteractionWidth()+")");
            }
            case TextDisplay display -> {
                desc = Component.text("(Text Display: \"", NamedTextColor.LIGHT_PURPLE)
                        .append(display.text())
                        .append(Component.text("\")", NamedTextColor.LIGHT_PURPLE));
            }
            case BlockDisplay display -> {
                if (DisplayUtils.isMaster(display)){
                    desc = Component.text("(Master Entity/Part)", NamedTextColor.GOLD);
                }
                else if (display.getBlock().getMaterial() == Material.AIR){
                    desc = Component.text("(Invisible Block Display | AIR, CAVE_AIR, or VOID_AIR)", NamedTextColor.GRAY);
                }
                else{
                    desc = Component.text("("+display.getBlock().getMaterial().key().value()+")", NamedTextColor.YELLOW);
                }
            }
            case ItemDisplay display -> {
                if (display.getItemStack().getType() == Material.AIR){
                    desc = Component.text("(Invisible Item Display | AIR, CAVE_AIR, or VOID_AIR)", NamedTextColor.GRAY);
                }
                else{
                    desc = Component.text("("+display.getItemStack().getType().key().value()+")", NamedTextColor.AQUA);
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + entity);
        }
        return desc;
    }

    private void displayPartInfo(Player p, ActivePart part, MultiPartSelection partSelection){
        int markDuration = 30;
        if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
            part.markInteraction(p, markDuration);
        }
        else{
            PacketUtils.setGlowing(p, part.getEntityId(), markDuration);
        }

        if (partSelection != null){
            int index = partSelection.indexOf(part)+1;
            int size = partSelection.getSize();
            Component ratio = Component.text("["+index+"/"+size+"] ", NamedTextColor.GOLD);
            p.sendMessage(DisplayAPI.pluginPrefix
                    .append(Component.text("Selected Part! ", NamedTextColor.GREEN))
                    .append(ratio)
                    .append(getPartInfo(part)));
        }

    }

}
