package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.ConsoleUsableSubCommand;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GroupSpawnAtCMD extends ConsoleUsableSubCommand {
    GroupSpawnAtCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("spawnat", parentSubCommand, Permission.GROUP_SPAWN);
        setTabComplete(2, "<x | ~>");
        setTabComplete(3, "<y | ~>");
        setTabComplete(4, "<z | ~>");
        setTabComplete(5, "<group-tag>");
        setTabComplete(6, TabSuggestion.STORAGES);
        setTabComplete(7, List.of("-world <world_name>", "-packet", "-force"));
        setTabComplete(8, List.of("-world <world_name>", "-packet", "-force"));
        setTabComplete(9, List.of("-world <world_name>", "-packet", "-force"));
        setTabComplete(10, List.of("-world <world_name>", "-packet", "-force"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 7) {
            sender.sendMessage(Component.text("Incorrect Usage! /deu group spawnat <x> <y> <z> <group-tag> <storage> [-world <world_name>] [-packet] [-force]", NamedTextColor.RED));
            return;
        }
        try{
            double x = getCoordinate(sender, args[2], 'x');
            double y = getCoordinate(sender, args[3], 'y');
            double z = getCoordinate(sender, args[4], 'z');
            String tag = args[5];
            String storage = args[6];

            OptionalArgs optionalArgs = new OptionalArgs(sender, args);
            World w;
            if (optionalArgs.worldName == null){
                if (sender instanceof Player player){
                    w = player.getWorld();
                }
                else{
                    sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You must supply a world name!", NamedTextColor.RED)));
                    sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("| Ex: \"/deu group spawnat <x> <y> <z> <distance> -world world_name", NamedTextColor.GRAY)));
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

            Location spawnLocation = new Location(w, x, y, z);
            GroupSpawnCMD.spawnGroup(sender, spawnLocation, tag, storage, optionalArgs.packet);
        }catch(NumberFormatException e){
            sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a number for each coordinate!", NamedTextColor.RED)));
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

    class OptionalArgs{
        String worldName = null;
        boolean packet = false;
        boolean force = false;

        OptionalArgs(CommandSender sender, String[] args){


            for (int i = 7; i < args.length; i++) {
                String arg = args[i];

                switch (arg.toLowerCase()) {
                    case "-packet" -> packet = true;
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
