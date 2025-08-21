package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.ConsoleUsableSubCommand;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.controller.DisplayControllerManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class GroupDismountCMD extends ConsoleUsableSubCommand {
    GroupDismountCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("dismount", parentSubCommand, Permission.GROUP_DISMOUNT);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 3) {
            if (!(sender instanceof Player)){
                sender.sendMessage(Component.text("Incorrect Console Usage! /mdis group dismount <player-name | entity-uuid> [-despawn]", NamedTextColor.RED));
                return;
            }
            else{
                sender.sendMessage(Component.text("Incorrect Usage! /mdis group dismount <-target | -selected | player-name | entity-uuid> [-despawn]", NamedTextColor.RED));
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

            SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(p);
            if (group == null) {
                DisplayEntityPluginCommand.noGroupSelection(p);
                return;
            }

            Entity vehicle = group.getVehicle();

            if (vehicle == null){
                sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Your selected group is not riding an entity!", NamedTextColor.RED)));
            }
            else{
                sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Successfully dismounted your selected group!", NamedTextColor.GREEN)));
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
        sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Successfully dismounted all groups riding the entity!", NamedTextColor.GREEN)));
        if (despawn) despawnMessage(sender);
    }

    private void despawnMessage(CommandSender sender){
        sender.sendMessage(Component.text("| Despawned dismounted group(s)", NamedTextColor.GRAY));
    }

    static void dismount(SpawnedDisplayEntityGroup group, boolean despawn){
        if (despawn){
            group.unregister(true, true);
        }
        else{
            group.dismount();
            group.unsetMachineState();
            group.setVerticalOffset(0);
            group.stopFollowingEntity();
        }
    }
}
