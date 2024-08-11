package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

class PartsCycleCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.PARTS_CYCLE)){
            return;
        }

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        if (args.length < 3){
            player.sendMessage(Component.text("/mdis parts cycle <first | prev | next> [jump]", NamedTextColor.GRAY));
            return;
        }

        SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(player);
        if (partSelection == null){
            partSelection = new SpawnedPartSelection(group);
        }
        DisplayGroupManager.setPartSelection(player, partSelection, false);

        int jump;
        if (args.length > 3){
            try{
                jump = Integer.getInteger(args[3]);
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
                singlePartSelected(player, partSelection);
            }
            case "prev", "previous" -> {
                partSelection.setToPreviousPart(jump);
                singlePartSelected(player, partSelection);
            }
            case "next" -> {
                partSelection.setToNextPart(jump);
                singlePartSelected(player, partSelection);
            }
            default ->{
                player.sendMessage(DisplayEntityPlugin.pluginPrefix+ ChatColor.RED+"Invalid Option! /mdis parts cycle <first | prev | next>");
            }
        }
    }

    private void singlePartSelected(Player p, SpawnedPartSelection partSelection){
        SpawnedDisplayEntityPart part = partSelection.getSelectedParts().getFirst();
        String desc = "";
        switch(part.getType()){
            case INTERACTION -> {
                Interaction i = (Interaction) part.getEntity();
                desc = ChatColor.YELLOW+"(Interaction, H:"+i.getInteractionHeight()+" W:"+i.getInteractionWidth()+")";
            }
            case TEXT_DISPLAY -> {
                TextDisplay display = (TextDisplay) part.getEntity();
                if (!display.getText().isBlank()) {
                    String text = display.getText();//.substring(0, endIndex);
                    desc = ChatColor.YELLOW+"(Text Display: "+text+ChatColor.YELLOW+")";
                }
            }

            case BLOCK_DISPLAY -> {
                if (part.isMaster()){
                    desc = ChatColor.AQUA+"(Master Entity)";
                }
                if (part.getMaterial() == Material.AIR){
                    desc = ChatColor.GRAY+"(Invisible Block Display | AIR, CAVE_AIR, or VOID_AIR)";
                }
            }
            case ITEM_DISPLAY -> {
                if (part.getMaterial() == Material.AIR){
                    desc = ChatColor.GRAY+"(Invisible Item Display | AIR, CAVE_AIR, or VOID_AIR)";
                }
            }
        }
        partSelection.glow(30, false);
        int index = partSelection.getGroup().getSpawnedParts().indexOf(partSelection.getSelectedParts().getFirst())+1;
        int size = partSelection.getGroup().getSpawnedParts().size();
        String ratio = ChatColor.GOLD+"["+index+"/"+size+"] ";
        p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Selected Part! "+ratio+desc);
    }

}
