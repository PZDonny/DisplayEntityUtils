package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.ConsoleUsableSubCommand;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.controller.DisplayController;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

class GroupRideCMD extends ConsoleUsableSubCommand {
    GroupRideCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("ride", parentSubCommand, Permission.GROUP_RIDE);
        setTabComplete(2, List.of("-target", "player-name | entity-uuid"));
        setTabComplete(3, "[group-tag]");
        setTabComplete(4, "[storage]");
        setTabComplete(5, "[controller-id]");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 2){
            sender.sendMessage(Component.text("Incorrect Usage! /deu group ride <-target | player-name | entity-uuid> [group-tag] [storage] [controller-id]", NamedTextColor.RED));
            return;
        }

        Entity vehicle = getVehicle(sender, args[2]);
        if (vehicle == null){
            return;
        }


        //Use Selected Group
        if (args.length < 5) {
            if (!(sender instanceof Player player)){
                sender.sendMessage(Component.text("Incorrect Console Usage! /deu group ride <-target | player-name | entity-uuid> [group-tag] [storage] [controller-id]", NamedTextColor.RED));
                return;
            }

            ActiveGroup<?> group = DisplayGroupManager.getSelectedGroup(player);
            if (group == null) {
                DisplayEntityPluginCommand.noGroupSelection(player);
                player.sendMessage(Component.text("Provide a group tag and storage location if you want to spawn a new group instead.", NamedTextColor.GRAY, TextDecoration.ITALIC));
                return;
            }

            attemptRide(vehicle, group, sender, args.length == 4 ? args[3] : null);
            return;
        }


        //Spawn A New Group
        LoadMethod loadMethod;
        try{
            loadMethod = LoadMethod.valueOf(args[4].toUpperCase());
        }
        catch(IllegalArgumentException e){
            sender.sendMessage(Component.text("Invalid Storage Method!", NamedTextColor.RED));
            sender.sendMessage(Component.text("Valid storage methods are local, mongodb, or mysql", NamedTextColor.GRAY));
            return;
        }

        DisplayEntityGroup savedGroup = DisplayGroupManager.getGroup(loadMethod, args[3]);
        if (savedGroup == null){
            sender.sendMessage(Component.text("Failed to find group in "+loadMethod.getDisplayName()+" storage", NamedTextColor.RED));
            return;
        }

        Location spawnLoc = vehicle.getLocation();
        spawnLoc.setPitch(0);
        SpawnedDisplayEntityGroup group = savedGroup.spawn(spawnLoc, GroupSpawnedEvent.SpawnReason.COMMAND);
        if (group == null){
            sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Failed to spawn the group! It was cancelled by another plugin!", NamedTextColor.RED)));
            return;
        }

        attemptRide(vehicle, group, sender, args.length == 6 ? args[5] : null);
    }

    private void attemptRide(Entity vehicle, ActiveGroup<?> group, CommandSender sender, String controllerID){
        if (group.getVehicle() == vehicle){
            sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("That group is already riding that entity!", NamedTextColor.RED)));
            return;
        }

        //Apply Controller
        if (controllerID != null){
            DisplayController controller = DisplayController.getController(controllerID);
            if (controller == null){
                sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Failed to find a controller with the specified ID! ("+controllerID+")", NamedTextColor.RED)));
                return;
            }

            if (controller.apply(vehicle, group)){
                sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Mounting your group on the entity with a controller! ("+controllerID+")", NamedTextColor.GREEN)));
            }
            else{
                sendFail(sender);
            }
        }
        else{
            if (group.rideEntity(vehicle)){
                sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Mounted your group on the entity!", NamedTextColor.GREEN)));
            }
            else{
                sendFail(sender);
            }
        }
    }

    private void sendFail(CommandSender sender){
        sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Failed to mount your group on the entity!", NamedTextColor.RED)));
        sender.sendMessage(Component.text("| If the group is both persistent and packet-based, this will always fail", NamedTextColor.GRAY, TextDecoration.ITALIC));
        sender.sendMessage(Component.text("| This can fail for other unknown reasons, or if the riding was cancelled by another plugin", NamedTextColor.GRAY, TextDecoration.ITALIC));
    }

    static Entity getVehicle(CommandSender sender, String value){
        Entity vehicle;
        if (value.equals("-target")){
            if (!(sender instanceof Player player)){
                sender.sendMessage(Component.text("You cannot use \"-target\" in console!", NamedTextColor.RED));
                return null;
            }
            //Apply to Target
            vehicle = player.getTargetEntity(5);
            if (vehicle == null){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You are not targeting an entity within 5 blocks!", NamedTextColor.RED)));
                return null;
            }
            if (DisplayUtils.isInGroup(vehicle)){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You cannot do that, that entity is in a group!", NamedTextColor.RED)));
                return null;
            }
        }

        else{
            try{
                vehicle = Bukkit.getEntity(UUID.fromString(value));
            }
            catch(IllegalArgumentException ex){
                vehicle = Bukkit.getPlayer(value);
            }

            if (vehicle == null){
                sender.sendMessage(Component.text("Vehicle Entity not found! Ensure you entered the correct Entity UUID / Player Name", NamedTextColor.RED));
            }
        }


        return vehicle;
    }
}
