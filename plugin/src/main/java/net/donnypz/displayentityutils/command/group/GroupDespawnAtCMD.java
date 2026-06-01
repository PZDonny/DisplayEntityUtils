package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.ConsoleUsableSubCommand;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GroupDespawnAtCMD extends ConsoleUsableSubCommand {
    GroupDespawnAtCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("despawnat", parentSubCommand, Permission.GROUP_DESPAWN);
        setTabComplete(2, "<x | ~>");
        setTabComplete(3, "<y | ~>");
        setTabComplete(4, "<z | ~>");
        setTabComplete(5, "<distance>");
        setTabComplete(6, List.of("-world <world_name>", "-packet <include | only>", "-force"));
        setTabComplete(7, List.of("-world <world_name>", "-packet <include | only>", "-force"));
        setTabComplete(8, List.of("-world <world_name>", "-packet <include | only>", "-force"));
        setTabComplete(9, List.of("-world <world_name>", "-packet <include | only>", "-force"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 6) {
            sender.sendMessage(Component.text("Incorrect Usage! /deu group despawnat <x> <y> <z> <distance> [-world <world-name>] [-packet <include | only>] [-force]", NamedTextColor.RED));
            return;
        }
        try{
            double x = getCoordinate(sender, args[2], 'x');
            double y = getCoordinate(sender, args[3], 'y');
            double z = getCoordinate(sender, args[4], 'z');
            double distance = Double.parseDouble(args[5]);

            OptionalArgs optionalArgs = new OptionalArgs(sender, args);
            if (!optionalArgs.valid) return;
            World w;
            if (optionalArgs.worldName == null){
                if (sender instanceof Player player){
                    w = player.getWorld();
                }
                else{
                    sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You must supply a world name!", NamedTextColor.RED)));
                    sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("| Ex: \"/deu group despawnat <x> <y> <z> <distance> -world world_name", NamedTextColor.GRAY)));
                    return;
                }
            }
            else{
                w = Bukkit.getWorld(optionalArgs.worldName);
            }

            if (w == null){
                sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("World with the given name is not loaded or does not exist!", NamedTextColor.RED)));
                return;
            }

            Location searchLocation = new Location(w, x, y, z);
            boolean hasGroups = false;
            if (optionalArgs.packetIncluded){
                Set<PacketDisplayEntityGroup> groups = PacketDisplayEntityGroup.getNearbyGroups(searchLocation, distance);
                hasGroups = !groups.isEmpty();
                if (hasGroups) groups.forEach(PacketDisplayEntityGroup::unregister);
            }

            if (!optionalArgs.packetOnly){
                Set<SpawnedDisplayEntityGroup> groups = DisplayGroupManager.getNearbySpawnedGroups(searchLocation, distance);
                if (!groups.isEmpty()){
                    hasGroups = true;
                    groups.forEach(sg -> sg.unregister(true, optionalArgs.force));
                }
            }
            if (!hasGroups){
                sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("There are no groups within the given range", NamedTextColor.RED)));
                return;
            }

            sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Despawned groups within the given range!", NamedTextColor.GRAY)));
        }catch(NumberFormatException e){
            sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter valid numbers for the coordinates and distance!", NamedTextColor.RED)));
        }
        catch (IllegalArgumentException e){
            sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You cannot use \"~\" in the console!", NamedTextColor.RED)));
        }
    }

    private double getCoordinate(CommandSender sender, String userInput, char coordinate){
        if (userInput.equals("~")){
            if (sender instanceof Player p ){
                Location pLoc = p.getLocation();
                if (coordinate == 'x') return pLoc.x();
                else if (coordinate == 'y') return pLoc.y();
                else return pLoc.z();
            }
            else{
                throw new IllegalArgumentException();
            }
        }
        else{
            return Double.parseDouble(userInput);
        }
    }

    static class OptionalArgs{
        boolean valid = true;
        String worldName = null;
        boolean packetOnly = false;
        boolean packetIncluded = false;
        boolean force = false;

        OptionalArgs(CommandSender sender, String[] args){


            for (int i = 6; i < args.length; i++) {
                String arg = args[i];

                switch (arg.toLowerCase()) {
                    case "-packet" -> {
                        if (packetOnly || packetIncluded) {
                            sender.sendMessage(Component.text(
                                    "-packet was specified multiple times.",
                                    NamedTextColor.RED));
                            return;
                        }
                        if (i + 1 >= args.length) {
                            sender.sendMessage(Component.text(
                                    "You must specify 'include' or 'only' after -packet.",
                                    NamedTextColor.RED));
                            valid = false;
                            return;
                        }

                        String mode = args[++i].toLowerCase();

                        switch (mode) {
                            case "include" -> packetIncluded = true;
                            case "only" -> packetOnly = true;
                            default -> {
                                sender.sendMessage(Component.text(
                                        "Packet mode must be 'include' or 'only'.",
                                        NamedTextColor.RED));
                                return;
                            }
                        }
                    }
                    case "-force" -> force = true;

                    case "-world" -> {
                        if (worldName != null) {
                            sender.sendMessage(Component.text(
                                    "-world was specified multiple times.",
                                    NamedTextColor.RED));
                            return;
                        }

                        if (i + 1 >= args.length) {
                            sender.sendMessage(Component.text(
                                    "You must specify a world after -world.",
                                    NamedTextColor.RED));
                            return;
                        }

                        worldName = args[++i];
                    }

                    default -> {
                        sender.sendMessage(Component.text(
                                "Unknown option: " + arg,
                                NamedTextColor.RED));
                        return;
                    }
                }
            }
        }
    }
}
