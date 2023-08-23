package com.pzdonny.displayentityutils;

import com.pzdonny.displayentityutils.managers.DisplayGroupManager;
import com.pzdonny.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import com.pzdonny.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import com.pzdonny.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

class MainCommand implements CommandExecutor {

    static void noSelection(Player p){
        p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"You have not selected a spawned display entity group!");
        p.sendMessage(ChatColor.GRAY+"/mdis select <interaction-distance>");
    }
    static void noPartSelection(Player p){
        p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"You have not selected a part!");
        p.sendMessage(ChatColor.GRAY+"/mdis cyclepart <first | prev | next>");
        p.sendMessage(ChatColor.GRAY+"/mdis selectparts <part-tag>");
    }

    static boolean hasPermission(Player p, String permission){
        if (!p.hasPermission(permission)){
            p.sendMessage(ChatColor.RED+"You do not have permission to do that!");
            return false;
        }
        return true;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 ){
            errorMessage(sender);
            return true;
        }

        if (!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED+"You cannot use this command in the console!");
            return true;
        }
        Player p = (Player) sender;

        if (args[0].equalsIgnoreCase("reload")){
            if (!hasPermission(p, "deu.reload")) return true;
            DisplayEntityPlugin.getInstance().reloadPlugin();
            sender.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.YELLOW+"Plugin Reloaded!");
            return true;
        }
        else if (args[0].equalsIgnoreCase("partshelp") || args[0].equalsIgnoreCase("parthelp")){
            if (!hasPermission(p, "deu.help")) return true;
            partsHelpMessage(sender);
            return true;
        }
        else if (args[0].equalsIgnoreCase("gettag")){
            if (!hasPermission(p, "deu.gettag")) return true;
            SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(p);
            if (group == null){
                noSelection(p);
                return true;
            }
            getTag((Player) sender, group);
            return true;
        }
        else if (args[0].equalsIgnoreCase("despawn")){
            if (!hasPermission(p, "deu.despawn")) return true;
            SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(p);
            if (group == null){
                noSelection(p);
                return true;
            }
            despawn(p, group);
            return true;
        }
        else if (args[0].equalsIgnoreCase("highlight")){
            if (!hasPermission(p, "deu.highlight")) return true;
            SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(p);
            if (group == null){
                noSelection(p);
                return true;
            }
            p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Highlighting selected entity!");
            group.highlight(100);
            return true;
        }
        else if (args[0].equalsIgnoreCase("highlightparts")){
            if (!hasPermission(p, "deu.highlight")) return true;
            SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(p);
            if (group == null){
                noSelection(p);
                return true;
            }
            SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(p);
            if (partSelection == null){
                noPartSelection(p);
                return true;
            }
            partSelection.highlight(80);
            p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Highlighting selected part(s)!");
            return true;
        }
        else if (args[0].equalsIgnoreCase("movehere")){
            if (!hasPermission(p, "deu.movehere")) return true;
            TransformationSubCommands.moveHere(p);
            return true;
        }
        else if (args[0].equalsIgnoreCase("clone")){
            if (!hasPermission(p, "deu.clone")) return true;
            SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(p);
            if (group == null){
                noSelection(p);
                return true;
            }
            clone(p, group);
            return true;
        }
        else if (args[0].equalsIgnoreCase("removeinteractions")){
            if (!hasPermission(p, "deu.removeinteractions")) return true;
            SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(p);
            if (group == null){
                noSelection(p);
                return true;
            }
            removeInteractions(p, group);
            return true;
        }
        else if (args[0].equalsIgnoreCase("getparttag")){
            if (!hasPermission(p, "deu.getparttag")) return true;
            getPartTag(p);
            return true;
        }
        else if (args.length < 2){
            errorMessage(sender);
            return true;
        }



        String tag = args[1];
        if (args[0].equalsIgnoreCase("settag")){
            if (!hasPermission(p, "deu.settag")) return true;
            SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(p);
            if (group == null){
                noSelection(p);
                return true;
            }
            setTag(p, group, tag);
            return true;
        }
        else if (args[0].equalsIgnoreCase("cycleparts")){
            if (!hasPermission(p, "deu.cycleparts")) return true;
            SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(p);
            if (group == null){
                noSelection(p);
                return true;
            }
            SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(p);
            if (partSelection == null){
                ArrayList<String> valid = new ArrayList<>();
                valid.add("first");
                valid.add("prev");
                valid.add("previous");
                valid.add("next");
                if (!valid.contains(args[1])){
                    p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Invalid Option! /mdis cycleparts <first | prev | next>");
                    return true;
                }
                partSelection = new SpawnedPartSelection(group);
                args[1] = "first";
                DisplayGroupManager.setPartSelection(p, partSelection, false);
            }
            else if (!partSelection.isValid()){
                p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Invalid part selection! Please try again!");
                return true;
            }
            switch(args[1]){
                case "first" -> {
                    partSelection.setToFirstPart();
                    singlePartSelected(p, partSelection);
                    return true;
                }
                case "prev", "previous" -> {
                    partSelection.setToPreviousPart();
                    singlePartSelected(p, partSelection);
                    return true;
                }
                case "next" -> {
                    partSelection.setToNextPart();
                    singlePartSelected(p, partSelection);
                    return true;
                }
                default ->{
                    p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Invalid Option! /mdis cycleparts <first | prev | next>");
                    return true;
                }
            }
        }
        else if (args[0].equalsIgnoreCase("setparttag")){
            if (!hasPermission(p, "deu.setparttag")) return true;
            SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(p);
            if (group == null){
                noSelection(p);
                return true;
            }
            SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(p);
            if (partSelection == null){
                noPartSelection(p);
                return true;
            }
            else if (!partSelection.isValid()){
                p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Invalid part selection! Please try again!");
                return true;
            }
            setPartTags(p, partSelection, args[1]);
            p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Part tag successfully set! "+ChatColor.WHITE+"(Part(s) Tagged: "+args[1]+")");
            return true;
        }

        else if (args[0].equalsIgnoreCase("selectnearest") || args[0].equalsIgnoreCase("select")){
            if (!hasPermission(p, "deu.select")) return true;
            SpawnedDisplayEntityGroup group;
            int interactionDistance = 0;
            try{
                interactionDistance = Integer.parseInt(args[1]);
            }
            catch(NumberFormatException e){
                p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Enter a number for the distance to select interaction entities");
                return true;
            }
            group = DisplayGroupManager.getSpawnedGroupNearLocation(p.getLocation(), 2, p);
            if (group != null){
                p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN + "Selection made!");
                DisplayGroupManager.setSelectedSpawnedGroup(p, group);
                DisplayGroupManager.removePartSelection(p);

                group.getUnaddedInteractionEntitiesInRange(interactionDistance, true);
                group.highlight(100);
            }
            return true;

        }
        else if (args[0].equalsIgnoreCase("addinteractions")){
            if (!hasPermission(p, "deu.addinteractions")) return true;
            SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(p);
            if (group == null){
                noSelection(p);
                return true;
            }
            int interactionDistance = 0;
            try{
                interactionDistance = Integer.parseInt(args[1]);
            }
            catch(NumberFormatException e){
                p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Enter a number for the distance to select interaction entities");
                return true;
            }
            addInteractions(p, group, interactionDistance);
            return true;
        }

        else if (args[0].equalsIgnoreCase("save")){
            if (!hasPermission(p, "deu.save")) return true;
            DataSubCommands.save(p, args[1]);
            return true;
        }

        else if (args[0].equalsIgnoreCase("selectparts")){
            if (!hasPermission(p, "deu.selectparts")) return true;
            SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(p);
            if (group == null){
                noSelection(p);
                return true;
            }
            SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(p);
            if (partSelection != null){
                partSelection.remove();
            }
            partSelection = new SpawnedPartSelection(group, tag);
            if (partSelection.getSelectedParts().isEmpty()){
                p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Failed to find parts with that part tag!");
                return true;
            }
            p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Part(s) successfully selected! "+ChatColor.WHITE+"(Part(s) Tagged: "+args[1]+")");
            DisplayGroupManager.setPartSelection(p, partSelection, false);
            partSelection.highlight(30);
            return true;
        }

        else if (args[0].equalsIgnoreCase("list")){
            if (!hasPermission(p, "deu.list")) return true;
            DisplayGroupManager.LoadMethod method;
            try{
                method = DisplayGroupManager.LoadMethod.valueOf(args[1].toUpperCase());
            }
            catch(IllegalArgumentException e){
                if (args[1].equalsIgnoreCase("all")){
                    sender.sendMessage(ChatColor.RED+"You cannot use \"all\" here!");
                }
                sender.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
                sender.sendMessage(ChatColor.GRAY+"/mdis list local");
                sender.sendMessage(ChatColor.GRAY+"/mdis list mongodb");
                sender.sendMessage(ChatColor.GRAY+"/mdis list mysql");
                return true;
            }


            List<String> tags = DisplayGroupManager.getDisplayEntityTags(method);
            p.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
            p.sendMessage(ChatColor.WHITE+"Storage Location: "+ChatColor.YELLOW+method.getDisplayName());
            if (tags.isEmpty()){
                p.sendMessage(ChatColor.RED+"That storage location does not have any saved display entity groups");
                return true;
            }

            int pageNumber = 1;
            if (args.length >= 3){
                try{
                    pageNumber = Math.abs(Integer.parseInt(args[2]));
                    if (pageNumber == 0) pageNumber = 1;
                }
                catch(NumberFormatException ignored){}
            }
            int end = pageNumber*7;
            int start = end-7;
            p.sendMessage(ChatColor.AQUA+"Page Number: "+pageNumber);
            for (int i = start; i <= end; i++){
                if (i >= tags.size()){
                    break;
                }
                p.sendMessage(ChatColor.WHITE+"- "+ChatColor.YELLOW+tags.get(i));
            }
            p.sendMessage("------------------------");
        }
        else if (args.length < 3){
            errorMessage(sender);
            return true;
        }
        else if (args[0].equalsIgnoreCase("delete")){
            if (!hasPermission(p, "deu.delete")) return true;
            DataSubCommands.delete(p, tag, args[2]);
        }
        else if (args[0].equalsIgnoreCase("spawn")){
            if (!hasPermission(p, "deu.spawn")) return true;
            DataSubCommands.spawn(p, tag, args[2]);
        }

        else if (args.length < 4){
            errorMessage(sender);
        }
        else if (args[0].equalsIgnoreCase("move")){
            if (!hasPermission(p, "deu.move")) return true;
            TransformationSubCommands.move(p, args);
            return true;
        }
        else if (args[0].equalsIgnoreCase("translate")){
            if (!hasPermission(p, "deu.translate")) return true;
            TransformationSubCommands.translate(p, args);
            return true;
        }
        else if (args[0].equalsIgnoreCase("translateparts")){
            if (!hasPermission(p, "deu.translate")) return true;
            TransformationSubCommands.translateParts(p, args);
            return true;
        }
        else{
            if (!hasPermission(p, "deu.help")) return true;
            errorMessage(p);
            return true;
        }
        return true;
    }


    static void errorMessage(CommandSender sender){
        sender.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
        sender.sendMessage(ChatColor.DARK_AQUA+"Valid storage is \"local\", \"mongodb\", \"mysql\", and \"all\"");
        sender.sendMessage();
        sender.sendMessage(ChatColor.GRAY+"/mdis help");
        sender.sendMessage(ChatColor.GRAY+"/mdis partshelp");
        sender.sendMessage(ChatColor.GRAY+"/mdis selectnearest <interaction-distance>");
        sender.sendMessage(ChatColor.GRAY+"/mdis addinteractions <interaction-distance>");
        sender.sendMessage(ChatColor.GRAY+"/mdis removeinteractions");
        sender.sendMessage(ChatColor.GRAY+"/mdis settag <tag>");
        sender.sendMessage(ChatColor.GRAY+"/mdis gettag");
        sender.sendMessage(ChatColor.GRAY+"/mdis highlight");
        sender.sendMessage(ChatColor.GRAY+"/mdis clone");
        sender.sendMessage(ChatColor.GRAY+"/mdis movehere");
        sender.sendMessage(ChatColor.GRAY+"/mdis move <direction> <distance> <tick-duration>");
        sender.sendMessage(ChatColor.GRAY+"/mdis translate <direction> <distance> <tick-duration>");
        sender.sendMessage(ChatColor.GRAY+"/mdis save <storage>");
        sender.sendMessage(ChatColor.GRAY+"/mdis delete <tag> <storage>");
        sender.sendMessage(ChatColor.GRAY+"/mdis spawn <tag> <storage>");
        sender.sendMessage(ChatColor.GRAY+"/mdis despawn");
        sender.sendMessage(ChatColor.GRAY+"/mdis list <storage> [page-number]");
        sender.sendMessage(ChatColor.GRAY+"/mdis reload");
    }

    static void partsHelpMessage(CommandSender sender){
        sender.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
        sender.sendMessage(ChatColor.AQUA+"\"Parts\" are each individual display/interaction entity that is spawned within the group");
        sender.sendMessage(ChatColor.AQUA+"Each part can be given a part tag, separate from the group tag to identify each individual part");
        sender.sendMessage(ChatColor.AQUA+"Multiple parts can have the same part tag");
        sender.sendMessage(ChatColor.GRAY+"This is mainly useful for API users / usage with addon plugins");
        sender.sendMessage();
        sender.sendMessage(ChatColor.GRAY+"/mdis cycleparts <first | prev | next>");
        sender.sendMessage(ChatColor.GRAY+"/mdis setparttag <part-tag>");
        sender.sendMessage(ChatColor.GRAY+"/mdis getparttag");
        sender.sendMessage(ChatColor.GRAY+"/mdis selectparts <part-tag>");
        sender.sendMessage(ChatColor.GRAY+"/mdis highlightparts");
        sender.sendMessage(ChatColor.GRAY+"/mdis transformparts <direction>");
    }

    static void invalidDirection(CommandSender sender){
        sender.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Invalid direction type!");
        sender.sendMessage(ChatColor.WHITE+"- "+ChatColor.YELLOW+"up");
        sender.sendMessage(ChatColor.WHITE+"- "+ChatColor.YELLOW+"down");
        sender.sendMessage(ChatColor.WHITE+"- "+ChatColor.YELLOW+"left");
        sender.sendMessage(ChatColor.WHITE+"- "+ChatColor.YELLOW+"right");
        sender.sendMessage(ChatColor.WHITE+"- "+ChatColor.YELLOW+"forward");
        sender.sendMessage(ChatColor.WHITE+"- "+ChatColor.YELLOW+"back");
    }


    private void despawn(Player p, SpawnedDisplayEntityGroup spawnedGroup){
        p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GRAY+"Successfully despawned selected display entity group!");
        DisplayGroupManager.deselectSpawnedGroup(p);
        DisplayGroupManager.removeSpawnedGroup(spawnedGroup);
    }

    private void setTag(Player p, SpawnedDisplayEntityGroup spawnedGroup, String newTag){
        spawnedGroup.setTag(newTag);
        p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Successfully tagged unsaved display entities! "+ChatColor.WHITE+"(Tagged: "+newTag+")");

    }

    private void setPartTags(Player p, SpawnedPartSelection partSelection, String newTag){
        partSelection.setPartTags(newTag);
        p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Successfully tagged selected part(s) of the selected group! "+ChatColor.WHITE+"(Part(s) Tagged: "+newTag+")");
    }

    private void getTag(Player p, SpawnedDisplayEntityGroup spawnedGroup){
        String tag = spawnedGroup.getTag();
        if (tag == null || tag.isBlank()){
            p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"This spawned display entity group does not have a tag!");
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
            return;
        }
        p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.WHITE+"(Tagged: "+tag+")");
    }


    private void clone(Player p, SpawnedDisplayEntityGroup spawnedGroup){
        SpawnedDisplayEntityGroup clonedGroup = spawnedGroup.clone(p.getLocation());
        if (clonedGroup == null){
            p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Failed to clone spawned display entity group!");
        }
        else{
            p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Successfully cloned spawned display entity group");
            DisplayGroupManager.setSelectedSpawnedGroup(p, clonedGroup);
            clonedGroup.highlight(80);
        }
    }
    private void addInteractions(Player p, SpawnedDisplayEntityGroup spawnedGroup, int interactionDistance){
        List<Interaction> interactions = spawnedGroup.getUnaddedInteractionEntitiesInRange(interactionDistance, true);
        if (interactions.isEmpty()){
            p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"There were no interaction entities to be added to the group!");
            return;
        }
        p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN + "Nearby interaction entities added and indicated with particles!");
        new BukkitRunnable(){
            int i = 0;
            public void run() {
                if (i >= 80){
                    cancel();
                    return;
                }
                for (Interaction interaction : interactions){
                    if (!interaction.isDead()){
                        Location loc = interaction.getLocation().clone();
                        loc.getWorld().spawnParticle(Particle.COMPOSTER, loc, 1, 0, 0,0 , 0);
                    }
                }
                i+=2;
            }
        }.runTaskTimer(DisplayEntityPlugin.getInstance(), 0, 2);
    }

    private void removeInteractions(Player p ,SpawnedDisplayEntityGroup group){
        p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Removed any interactions entities attached to the spawned display entity group");
        group.removeInteractionEntities();
    }

    private void getPartTag(Player p){
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(p);
        if (group == null){
            noSelection(p);
            return;
        }
        SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(p);
        if (partSelection == null){
            noPartSelection(p);
            return;
        }
        String partTag = partSelection.getPartTag();
        if (partTag == null){
            p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Failed to find a part tag for your part selection!");
            return;
        }
        p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.WHITE+"(Part(s) Tagged: "+partTag+")");
    }

    private void singlePartSelected(Player p, SpawnedPartSelection partSelection){
        SpawnedDisplayEntityPart part = partSelection.getSelectedParts().get(0);
        String desc = "";
        switch(part.getType()){
            case INTERACTION -> {
                Interaction i = (Interaction) part.getEntity();
                desc = ChatColor.RED+"(Interaction, H:"+i.getInteractionHeight()+" W:"+i.getInteractionWidth()+")";
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
            }
        }
        partSelection.highlight(30);
        int index = partSelection.getGroup().getSpawnedParts().indexOf(partSelection.getSelectedParts().get(0))+1;
        int size = partSelection.getGroup().getSpawnedParts().size();
        String ratio = ChatColor.GOLD+"["+index+"/"+size+"] ";
        p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Selected Part! "+ratio+desc);
    }
}
