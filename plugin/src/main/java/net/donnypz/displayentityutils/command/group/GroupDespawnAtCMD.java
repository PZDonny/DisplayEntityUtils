package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.ConsoleUsableSubCommand;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
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

import java.util.List;
import java.util.Set;

public class GroupDespawnAtCMD extends ConsoleUsableSubCommand {
    GroupDespawnAtCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("despawnat", parentSubCommand, Permission.GROUP_DESPAWN);
        setTabComplete(2, TabSuggestion.X_COORDINATE);
        setTabComplete(3, TabSuggestion.Y_COORDINATE);
        setTabComplete(4, TabSuggestion.Z_COORDINATE);
        setTabComplete(5, "<distance>");
        addFlag("-force");
        addOption("-world", "<world_name>");
        addOption("-packet", List.of("include", "only"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!hasMinimumArguments(sender, args)) return;

        try{
            double x = getCoordinate(sender, args[2], 'x');
            double y = getCoordinate(sender, args[3], 'y');
            double z = getCoordinate(sender, args[4], 'z');
            double distance = Double.parseDouble(args[5]);

            OptionalArguments optionalArgs = getOptionalArguments(sender, args);
            if (!optionalArgs.isValidOptions()) return;

            World w;
            String worldName = optionalArgs.getOption("-world");
            if (worldName.isEmpty()){
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
                w = Bukkit.getWorld(worldName);
            }

            if (w == null){
                sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("World with the given name is not loaded or does not exist!", NamedTextColor.RED)));
                return;
            }

            Location searchLocation = new Location(w, x, y, z);
            boolean hasGroups = false;
            String packetOption = optionalArgs.getOption("-packet");

            if (packetOption.equals("included") || packetOption.equals("only")){
                Set<PacketDisplayEntityGroup> groups = PacketDisplayEntityGroup.getNearbyGroups(searchLocation, distance);
                hasGroups = !groups.isEmpty();
                if (hasGroups) groups.forEach(PacketDisplayEntityGroup::unregister);
            }

            if (!packetOption.equals("only")){
                Set<SpawnedDisplayEntityGroup> groups = DisplayGroupManager.getNearbySpawnedGroups(searchLocation, distance);
                if (!groups.isEmpty()){
                    hasGroups = true;
                    groups.forEach(sg -> sg.unregister(true, optionalArgs.hasFlag("-force")));
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
}
