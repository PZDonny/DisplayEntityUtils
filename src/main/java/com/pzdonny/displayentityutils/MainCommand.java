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
        p.sendMessage(ChatColor.GRAY+"/mdis group selectnearest <interaction-distance>");
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
        if (!(sender instanceof Player p)){
            sender.sendMessage(ChatColor.RED+"You cannot use this command in the console!");
            return true;
        }

        if (args.length == 0 ){
            mainCommandList(sender);
            return true;
        }
        String firstArg = args[0];
        if (firstArg.equalsIgnoreCase("help")){
            mainCommandList(sender);
            return true;
        }

        else if (firstArg.equalsIgnoreCase("reload")){
            if (!hasPermission(p, "deu.reload")){
                return true;
            }
            DisplayEntityPlugin.getInstance().reloadPlugin(false);
            sender.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.YELLOW+"Plugin Reloaded!");
            return true;
        }

        else if (firstArg.equalsIgnoreCase("list")){
            if (!hasPermission(p, "deu.list")){
                return true;
            }
            if (args.length == 1){
                p.sendMessage(ChatColor.RED+"Incorrect Usage! /mdis list <storage> [page-number]");
                return true;
            }
            DisplayGroupManager.LoadMethod method;
            try{
                method = DisplayGroupManager.LoadMethod.valueOf(args[1].toUpperCase());
            }
            catch(IllegalArgumentException e){
                if (args[1].equalsIgnoreCase("all")){
                    sender.sendMessage(ChatColor.RED+"You cannot use \"all\" here!");
                }
                sender.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
                p.sendMessage(ChatColor.RED+"Invalid Storage Location!");
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

        else if (firstArg.equalsIgnoreCase("parts")){
            if (!hasPermission(p, "deu.help")){
                return true;
            }

            if (args.length == 1){
                partsHelp(p);
                return true;
            }

            SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(p);
            if (group == null){
                noSelection(p);
                return true;
            }

            String arg2 = args[1];
            switch (arg2){
                case "cycle" -> {
                    if (!hasPermission(p, "deu.parts.cycle")){
                        return true;
                    }
                    if (args.length == 2){
                        p.sendMessage(ChatColor.GRAY+"/mdis parts cycle <first | prev | next>");
                        return true;
                    }
                    SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(p);
                    if (partSelection == null){
                        partSelection = new SpawnedPartSelection(group);
                    }
                    //DisplayGroupManager.setPartSelection(p, partSelection, false);

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
                            p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Invalid Option! /mdis parts cycle <first | prev | next>");
                            return true;
                        }
                    }
                }

                case "select" -> {
                    if (!hasPermission(p, "deu.parts.select")){
                        return true;
                    }
                    SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(p);
                    if (partSelection != null){
                        partSelection.remove();
                    }
                    if (args.length < 3){
                        p.sendMessage(ChatColor.RED+"/mdis parts select <part-tag>");
                    }
                    partSelection = new SpawnedPartSelection(group, args[2]);
                    if (partSelection.getSelectedParts().isEmpty()){
                        p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Failed to find parts with that part tag!");
                        return true;
                    }
                    p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Part(s) successfully selected! "+ChatColor.WHITE+"(Part(s) Tagged: "+args[1]+")");
                    DisplayGroupManager.setPartSelection(p, partSelection, false);
                    partSelection.highlight(30);
                    return true;
                }

                case "settag" -> {
                    if (!hasPermission(p, "deu.parts.settag")){
                        return true;
                    }
                    SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(p);
                    if (partSelection == null){
                        noPartSelection(p);
                        return true;
                    }
                    if (!partSelection.isValid()){
                        p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Invalid part selection! Please try again!");
                        return true;
                    }
                    if (args.length == 3){
                        setPartTags(p, partSelection, args[2]);
                        return true;
                    }
                    else{
                        p.sendMessage(ChatColor.RED+"Provide a part tag! /mdis parts settag <part-tag>");
                    }

                }

                case "gettag" -> {
                    if (!hasPermission(p, "deu.parts.gettag")){
                        return true;
                    }
                    getPartTag(p, group);
                }

                case "remove" -> {
                    if (!hasPermission(p, "deu.parts.remove")){
                        return true;
                    }
                    removePart(p, group);
                }

                case "translate" -> {
                    if (!hasPermission(p, "deu.parts.translate")){
                        return true;
                    }
                    SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(p);
                    if (partSelection == null){
                        noPartSelection(p);
                        return true;
                    }
                    if (args.length < 5){
                        sender.sendMessage(ChatColor.RED+"/mdis parts translate <direction> <distance> <tick-duration>");
                        return true;
                    }
                    TransformationSubCommands.translateParts(p, args, partSelection);
                }

                case "glow" -> {
                    if (!hasPermission(p, "deu.parts.glow")){
                        return true;
                    }
                    SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(p);
                    if (partSelection == null){
                        noPartSelection(p);
                        return true;
                    }
                    partSelection.highlight(80);
                    p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Glowing selected part(s)!");
                }
                case "removeinteractioncommand" ->{
                    if (!hasPermission(p, "deu.parts.interactioncmd")){
                        return true;
                    }
                    removeInteractionCommand(p);
                }
                case "setinteractioncommand" -> {
                    if (!hasPermission(p, "deu.parts.interactioncmd")){
                        return true;
                    }
                    if (args.length < 3){
                        p.sendMessage(ChatColor.RED+"/mdis parts setinteractioncommand <command>");
                        return true;
                    }
                    setInteractionCommand(p, args);
                }
            }
        }

        else if (firstArg.equalsIgnoreCase("group")){
            if (!hasPermission(p, "deu.help")){
                return true;
            }
            if (args.length == 1){
                groupHelp(p);
                return true;
            }

            SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(p);
            String arg2 = args[1];
            switch (arg2){
                case "spawn" -> {
                    if (!hasPermission(p, "deu.group.spawn")){
                        return true;
                    }
                    if (args.length < 4){
                        sender.sendMessage(ChatColor.RED+"Incorrect Usage! /mdis group spawn <group-tag> <storage>");
                        return true;
                    }
                    DataSubCommands.spawn(p, args[2], args[3]);
                }

                case "save" -> {
                    if (!hasPermission(p, "deu.group.save")){
                        return true;
                    }
                    if (args.length < 3){
                        sender.sendMessage(ChatColor.RED+"Incorrect Usage /mdis group save <storage>");
                        return true;
                    }
                    DataSubCommands.save(p, args[2].toLowerCase());
                }

                case "delete" -> {
                    if (!hasPermission(p, "deu.group.delete")){
                        return true;
                    }
                    if (args.length < 4){
                        sender.sendMessage(ChatColor.RED+"/mdis group delete <group-tag> <storage-location>");
                        return true;
                    }
                    DataSubCommands.delete(p, args[2], args[3].toLowerCase());
                }

                case "selectnearest" -> {
                    if (!hasPermission(p, "deu.group.selectnearest")){
                        return true;
                    }
                    int interactionDistance = 0;
                    if (args.length >= 3){
                        try{
                            interactionDistance = Integer.parseInt(args[2]);
                        }
                        catch(NumberFormatException e){
                            p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Enter a number for the distance to select interaction entities");
                            return true;
                        }
                    }

                    group = DisplayGroupManager.getSpawnedGroupNearLocation(p.getLocation(), 2, p);
                    if (group != null){
                        p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN + "Selection made!");
                        DisplayGroupManager.setSelectedSpawnedGroup(p, group);
                        DisplayGroupManager.removePartSelection(p);

                        group.getUnaddedInteractionEntitiesInRange(interactionDistance, true);
                        group.highlight(100);
                    }
                }

                case "movehere" -> {
                    if (!hasPermission(p, "deu.group.movehere")){
                        return true;
                    }
                    TransformationSubCommands.moveHere(p);
                }

                case "move" -> {
                    if (!hasPermission(p, "deu.group.move")){
                        return true;
                    }
                    if (group == null){
                        noSelection(p);
                        return true;
                    }
                    if (args.length < 5){
                        sender.sendMessage(ChatColor.RED+"/mdis move translate <direction> <distance> <tick-duration>");
                        return true;
                    }
                    TransformationSubCommands.move(p, args, group);
                }

                case "translate" -> {
                    if (!hasPermission(p, "deu.group.translate")){
                        return true;
                    }
                    if (group == null){
                        noSelection(p);
                        return true;
                    }
                    if (args.length < 5){
                        sender.sendMessage(ChatColor.RED+"/mdis group translate <direction> <distance> <tick-duration>");
                        return true;
                    }
                    TransformationSubCommands.translate(p, args, group);
                }

                case "setyaw" -> {
                    if (!hasPermission(p, "deu.translate")){
                        return true;
                    }
                    if (group == null){
                        noSelection(p);
                        return true;
                    }
                    if (args.length < 3){
                        sender.sendMessage(ChatColor.RED+"/mdis group setyaw <yaw>");
                        return true;
                    }
                    TransformationSubCommands.setYaw(p, args[2]);
                    return true;
                }

                case "removeinteractions" -> {
                    if (!hasPermission(p, "deu.group.removeinteractions")){
                        return true;
                    }
                    if (group == null){
                        noSelection(p);
                        return true;
                    }
                    removeInteractions(p, group);
                    return true;
                }

                case "settag" -> {
                    if (!hasPermission(p, "deu.group.settag")){
                        return true;
                    }
                    if (group == null){
                        noSelection(p);
                        return true;
                    }

                    if (args.length == 3){
                        setTag(p, group, args[2]);
                        return true;
                    }
                    else{
                        p.sendMessage(ChatColor.RED+"Provide a part tag! /mdis group settag <part-tag>");
                        return true;
                    }
                }

                case "gettag" -> {
                    if (!hasPermission(p, "deu.group.gettag")){
                        return true;
                    }
                    if (group == null){
                        noSelection(p);
                        return true;
                    }
                    getTag((Player) sender, group);
                    return true;
                }

                case "glow" -> {
                    if (!hasPermission(p, "deu.group.glow")) return true;
                    if (group == null){
                        noSelection(p);
                        return true;
                    }
                    p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Glowing selected spawned display entity group!");
                    group.highlight(100);
                    return true;
                }

                case "clone" -> {
                    if (!hasPermission(p, "deu.group.clone")){
                        return true;
                    }
                    if (group == null){
                        noSelection(p);
                        return true;
                    }
                    clone(p, group);
                    return true;
                }

                case "despawn" -> {
                    if (!hasPermission(p, "deu.group.despawn")) {
                        return true;
                    }
                    if (group == null){
                        noSelection(p);
                        return true;
                    }
                    despawn(p, group);
                    return true;
                }
            }
        }
        else{
            mainCommandList(p);
        }
        return true;
    }


    static void mainCommandList(CommandSender sender){
        sender.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
        sender.sendMessage(ChatColor.DARK_AQUA+"Valid storage is \"local\", \"mongodb\", \"mysql\", and \"all\"");
        sender.sendMessage();
        sender.sendMessage(ChatColor.GRAY+"/mdis help");
        sender.sendMessage(ChatColor.GRAY+"/mdis parts");
        sender.sendMessage(ChatColor.GRAY+"/mdis group");
        sender.sendMessage(ChatColor.GRAY+"/mdis list <storage> [page-number]");
        sender.sendMessage(ChatColor.GRAY+"/mdis reload");
    }

    static void groupHelp(CommandSender sender){
        sender.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
        sender.sendMessage(ChatColor.WHITE+"-Group Help");
        sender.sendMessage(ChatColor.GRAY+"/mdis group selectnearest <interaction-distance>");
        sender.sendMessage(ChatColor.GRAY+"/mdis group spawn <group-tag> <storage>");
        sender.sendMessage(ChatColor.GRAY+"/mdis group despawn");
        sender.sendMessage(ChatColor.GRAY+"/mdis group save <storage-location>");
        sender.sendMessage(ChatColor.GRAY+"/mdis group delete <group-tag> <storage-location>");
        sender.sendMessage(ChatColor.GRAY+"/mdis group removeinteractions");
        sender.sendMessage(ChatColor.GRAY+"/mdis group settag <group-tag>");
        sender.sendMessage(ChatColor.GRAY+"/mdis group gettag");
        sender.sendMessage(ChatColor.GRAY+"/mdis group setyaw <yaw>");
        sender.sendMessage(ChatColor.GRAY+"/mdis group clone");
        sender.sendMessage(ChatColor.GRAY+"/mdis group move <direction> <distance> <tick-duration>");
        sender.sendMessage(ChatColor.GRAY+"/mdis group translate <direction> <distance> <tick-duration>");
        sender.sendMessage(ChatColor.GRAY+"/mdis group movehere");
        sender.sendMessage(ChatColor.GRAY+"/mdis group glow");
    }

    static void partsHelp(CommandSender sender){
        sender.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
        sender.sendMessage(ChatColor.AQUA+"\"Parts\" are each individual display/interaction entity that is spawned within the group");
        sender.sendMessage(ChatColor.AQUA+"Each part can be given a part tag to identify each individual part");
        sender.sendMessage(ChatColor.AQUA+"Parts can share the same part tag to create part selections");
        sender.sendMessage(ChatColor.GRAY+"This is mainly useful for API users / usage with addon plugins");
        sender.sendMessage();
        sender.sendMessage(ChatColor.GRAY+"/mdis parts cycle <first | prev | next>");
        sender.sendMessage(ChatColor.GRAY+"/mdis parts settag <part-tag>");
        sender.sendMessage(ChatColor.GRAY+"/mdis parts gettag");
        sender.sendMessage(ChatColor.GRAY+"/mdis parts select <part-tag>");
        sender.sendMessage(ChatColor.GRAY+"/mdis parts remove");
        sender.sendMessage(ChatColor.GRAY+"/mdis parts glow");
        sender.sendMessage(ChatColor.GRAY+"/mdis parts translate <direction> <distance> <tick-duration>");
        sender.sendMessage(ChatColor.GRAY+"/mdis parts setinteractioncommand");
        sender.sendMessage(ChatColor.GRAY+"/mdis parts removeinteractioncommand");
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
        p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Successfully tagged spawned display entity group! "+ChatColor.WHITE+"(Tagged: "+newTag+")");
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
    /*private void addInteractions(Player p, SpawnedDisplayEntityGroup spawnedGroup, int interactionDistance){
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
    }*/

    private void removeInteractions(Player p ,SpawnedDisplayEntityGroup group){
        p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Removed any interactions entities attached to the spawned display entity group");
        group.removeInteractionEntities();
    }

    private void getPartTag(Player p, SpawnedDisplayEntityGroup group){
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

    private void removePart(Player p, SpawnedDisplayEntityGroup group){
        if (group == null){
            noSelection(p);
            return;
        }
        SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(p);
        if (partSelection == null){
            noPartSelection(p);
            return;
        }
        for (SpawnedDisplayEntityPart part : partSelection.getSelectedParts()){
            if (part.isMaster()){
                p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"You cannot despawn the master part by itself!");
                continue;
            }
            part.remove(true);
        }
        if (partSelection.getGroup().getSpawnedParts().size() <= 1){
            p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.YELLOW+"Despawning your group, not enough parts remain");
            partSelection.getGroup().despawn();
            return;
        }
        partSelection.remove();
    }

    private void setInteractionCommand(Player p , String[] args){
        SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(p);
        if (partSelection == null){
            noPartSelection(p);
            return;
        }
        if (partSelection.getSelectedParts().size() > 1){
            p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"You can only do this with one part selected");
            return;
        }
        if (partSelection.getSelectedParts().get(0).getType() != SpawnedDisplayEntityPart.PartType.INTERACTION){
            p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"You can only do this with interaction entities");
        }

        Interaction interaction = (Interaction) partSelection.getSelectedParts().get(0).getEntity();
        StringBuilder builder = new StringBuilder();
        for (int i = 2; i < args.length; i++){
            builder.append(args[i]);
            if (i+1 != args.length) builder.append(" ");
        }
        String command = builder.toString();
        DisplayGroupManager.setInteractionCommand(interaction, command);
        p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Command Set! "+ChatColor.YELLOW+"("+command+")");
    }

    private void removeInteractionCommand(Player p){
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
        if (partSelection.getSelectedParts().size() > 1){
            p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"You can only do this with one part selected");
            return;
        }
        if (partSelection.getSelectedParts().get(0).getType() != SpawnedDisplayEntityPart.PartType.INTERACTION){
            p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"You can only do this with interaction entities");
        }

        Interaction interaction = (Interaction) partSelection.getSelectedParts().get(0).getEntity();
        DisplayGroupManager.removeInteractionCommand(interaction);
        p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.YELLOW+"Removed any existing commands from interaction entity");
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
