package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

class PartsCycleCMD extends PlayerSubCommand {
    PartsCycleCMD() {
        super(Permission.PARTS_CYCLE);
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

        SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(player);
        if (partSelection == null){
            partSelection = new SpawnedPartSelection(group);
            DisplayGroupManager.setPartSelection(player, partSelection, false);
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
                displayPartInfo(player, partSelection);
            }
            case "last" -> {
                partSelection.setToLastPart();
                displayPartInfo(player, partSelection);
            }
            case "prev", "previous" -> {
                partSelection.setToPreviousPart(jump);
                displayPartInfo(player, partSelection);
            }
            case "next" -> {
                partSelection.setToNextPart(jump);
                displayPartInfo(player, partSelection);
            }
            default ->{
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Invalid Option! /mdis parts cycle <first | prev | next | last>", NamedTextColor.RED)));
            }
        }
    }

    private void displayPartInfo(Player p, SpawnedPartSelection partSelection){
        SpawnedDisplayEntityPart part = partSelection.getSelectedPart();
        Component desc = Component.empty();
        switch(part.getType()){
            case INTERACTION -> {
                Interaction i = (Interaction) part.getEntity();
                desc = MiniMessage.miniMessage().deserialize("<yellow>(Interaction, H: "+i.getInteractionHeight()+" W:"+i.getInteractionWidth()+")");
            }

            case TEXT_DISPLAY -> {
                TextDisplay display = (TextDisplay) part.getEntity();
                if (!display.getText().isBlank()) {
                    desc = Component.text("(Text Display: ", NamedTextColor.YELLOW).append(display.text()).append(Component.text(")", NamedTextColor.YELLOW));
                }
            }

            case BLOCK_DISPLAY -> {
                if (part.isMaster()){
                    desc = Component.text("(Master Entity/Part)", NamedTextColor.AQUA);
                }
                if (part.getMaterial() == Material.AIR){
                    desc = Component.text("(Invisible Block Display | AIR, CAVE_AIR, or VOID_AIR)", NamedTextColor.GRAY);
                }
            }

            case ITEM_DISPLAY -> {
                if (part.getMaterial() == Material.AIR){
                    desc = Component.text("(Invisible Item Display | AIR, CAVE_AIR, or VOID_AIR)", NamedTextColor.GRAY);
                }
            }
        }
        part.glow(30, false);
        int index = partSelection.indexOf(part)+1;
        int size = partSelection.getSize();
        Component ratio = Component.text("["+index+"/"+size+"] ", NamedTextColor.GOLD);
        p.sendMessage(DisplayEntityPlugin.pluginPrefix
                .append(Component.text("Selected Part! ", NamedTextColor.GREEN))
                .append(ratio)
                .append(desc));
    }

}
