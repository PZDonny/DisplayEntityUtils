package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.ConsoleUsableSubCommand;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.controller.DisplayControllerManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class GroupDismountCMD extends ConsoleUsableSubCommand {
    GroupDismountCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("dismount", parentSubCommand, Permission.GROUP_DISMOUNT);
        setTabComplete(2, List.of("-target", "-selected", "player-name", "entity-uuid"));
        setTabComplete(3, "-despawn");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 3) {
            if (!(sender instanceof Player)){
                sender.sendMessage(Component.text("Incorrect Console Usage! /deu group dismount <player-name | entity-uuid> [-despawn]", NamedTextColor.RED));
                return;
            }
            else{
                sender.sendMessage(Component.text("Incorrect Usage! /deu group dismount <-target | -selected | player-name | entity-uuid> [-despawn]", NamedTextColor.RED));
            }
            return;
        }

        boolean despawn = args.length == 4 && args[3].equalsIgnoreCase("-despawn");

        String type = args[2];
        if (type.equalsIgnoreCase("-selected")){
            if (!(sender instanceof Player p)){
                sender.sendMessage(Component.text("You cannot use \"-selected\" in console!", NamedTextColor.RED));
                return;
            }

            ActiveGroup<?> group = DisplayGroupManager.getSelectedGroup(p);
            if (group == null) {
                DisplayEntityPluginCommand.noGroupSelection(p);
                return;
            }

            Entity vehicle = group.getVehicle();

            if (vehicle == null){
                sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Your selected group is not riding an entity!", NamedTextColor.RED)));
            }
            else{
                sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Dismounted your selected group!", NamedTextColor.GREEN)));
                dismount(group, despawn);
                if (despawn) despawnMessage(sender);
                DisplayControllerManager.unregisterEntity(vehicle);
            }
            return;
        }

        Entity vehicle = GroupRideCMD.getVehicle(sender, type);
        if (vehicle == null){
            return;
        }

        for (SpawnedDisplayEntityGroup g : DisplayUtils.getGroupPassengers(vehicle)){
            dismount(g, despawn);
        }

        DisplayControllerManager.unregisterEntity(vehicle);
        sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Dismounted all non-packet groups riding the entity!", NamedTextColor.GREEN)));
        if (despawn) despawnMessage(sender);
    }

    private void despawnMessage(CommandSender sender){
        sender.sendMessage(Component.text("| Despawned dismounted group(s)", NamedTextColor.GRAY));
    }

    static void dismount(ActiveGroup<?> group, boolean despawn){
        if (despawn){
            if (group instanceof SpawnedDisplayEntityGroup sg){
                sg.unregister(true, true);
            }
            else if (group instanceof PacketDisplayEntityGroup pdeg){
                pdeg.unregister();
            }
        }
        else{
            group.dismount();
            group.unsetMachineState();
            group.setRideOffset(new Vector());
            group.stopFollowingEntity();
        }
    }
}
