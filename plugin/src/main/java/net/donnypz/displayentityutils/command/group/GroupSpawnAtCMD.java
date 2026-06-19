package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.ConsoleUsableSubCommand;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GroupSpawnAtCMD extends ConsoleUsableSubCommand {
    GroupSpawnAtCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("spawnat", parentSubCommand, Permission.GROUP_SPAWN);
        setTabComplete(2, TabSuggestion.X_COORDINATE);
        setTabComplete(3, TabSuggestion.Y_COORDINATE);
        setTabComplete(4, TabSuggestion.Z_COORDINATE);
        setTabComplete(5, "<group-tag>");
        setTabComplete(6, TabSuggestion.STORAGES);
        addFlag("-packet");
        addFlag("-force");
        addOption("-world", "<world_name>");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!hasMinimumArguments(sender, args)) return;

        try{
            double x = getCoordinate(sender, args[2], 'x');
            double y = getCoordinate(sender, args[3], 'y');
            double z = getCoordinate(sender, args[4], 'z');
            String tag = args[5];
            String storage = args[6];

            OptionalArguments optionalArgs = getOptionalArguments(sender, args);
            World w;
            String worldName = optionalArgs.getOption("-world");
            if (worldName.isEmpty()){
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
                w = Bukkit.getWorld(worldName);
            }

            if (w == null){
                sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("World with the given name is not loaded or does not exist!", NamedTextColor.RED)));
                return;
            }

            Location spawnLocation = new Location(w, x, y, z);
            GroupSpawnCMD.spawnGroup(sender, spawnLocation, tag, storage, optionalArgs.hasFlag("-packet"));
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

    @Override
    protected String getDescription() {
        return """
                Spawn a saved display entity group/model from a storage location at a specified location. \
                
                "-world <world_name>" will spawn the group in the provided world\
                
                "-packet" will spawn the group/model using packets\
                
                "-force" will force the location's chunk to load, if unloaded. Unneeded if spawned using packets
                """;
    }
}
