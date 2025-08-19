package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ServerSideSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.PacketUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;

class PartsCycleCMD extends PlayerSubCommand {
    PartsCycleCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("cycle", parentSubCommand, Permission.PARTS_CYCLE);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        if (args.length < 3){
            player.sendMessage(Component.text("/mdis parts cycle <first | prev | next | last> [jump]", NamedTextColor.GRAY));
            return;
        }

        ServerSideSelection sel = DisplayGroupManager.getPartSelection(player);
        SpawnedPartSelection partSelection;
        if (PartsCMD.isUnwantedSingleSelection(player, sel)){
            return;
        }
        else if (sel == null){
            partSelection = new SpawnedPartSelection(group);
            DisplayGroupManager.setPartSelection(player, partSelection, false);
        }
        else{
            partSelection = (SpawnedPartSelection) sel;
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

    static Component getPartInfo(@NotNull Entity entity){
        Component desc = Component.empty();
        switch(entity){
            case Interaction i -> {
                desc = MiniMessage.miniMessage().deserialize("<dark_aqua>(Interaction, H: "+i.getInteractionHeight()+" W:"+i.getInteractionWidth()+")");
            }

            case TextDisplay display -> {
                if (!display.getText().isBlank()) {
                    desc = Component.text("(Text Display: \"", NamedTextColor.LIGHT_PURPLE)
                            .append(display.text())
                            .append(Component.text("\")", NamedTextColor.LIGHT_PURPLE));
                }
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

    private void displayPartInfo(Player p, SpawnedDisplayEntityPart part, SpawnedPartSelection partSelection){
        int markDuration = 30;
        if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
            part.markInteraction(p, markDuration);
        }
        else{
            PacketUtils.setGlowing(p, part.getEntity().getEntityId(), markDuration);
        }

        if (partSelection != null){
            int index = partSelection.indexOf(part)+1;
            int size = partSelection.getSize();
            Component ratio = Component.text("["+index+"/"+size+"] ", NamedTextColor.GOLD);
            p.sendMessage(DisplayAPI.pluginPrefix
                    .append(Component.text("Selected Part! ", NamedTextColor.GREEN))
                    .append(ratio)
                    .append(getPartInfo(part.getEntity())));
        }

    }

}
