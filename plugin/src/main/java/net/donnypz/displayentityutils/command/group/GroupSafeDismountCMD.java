package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.ConsoleUsableSubCommand;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.controller.DisplayControllerManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class GroupSafeDismountCMD extends ConsoleUsableSubCommand {
    GroupSafeDismountCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("safedismount", parentSubCommand, Permission.GROUP_DISMOUNT);
        setTabComplete(2, List.of("-target", "-selected", "player-name", "entity-uuid"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 3) {
            if (!(sender instanceof Player)){
                sender.sendMessage(Component.text("Incorrect Console Usage! /deu group safedismount <player-name | entity-uuid>", NamedTextColor.RED));
                return;
            }
            else{
                sender.sendMessage(Component.text("Incorrect Usage! /deu group safedismount <-target | -selected | player-name | entity-uuid>", NamedTextColor.RED));
            }
            return;
        }

        String type = args[2];
        boolean hadAI;
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
                sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Wait a moment while your group is safely dismounted...", NamedTextColor.YELLOW)));
                if (vehicle instanceof LivingEntity le){
                    hadAI = le.hasAI();
                }
                else{
                    hadAI = false;
                }
                vehicle.setRotation(vehicle.getYaw(), 0);
                DisplayAPI.getScheduler().runLater(() -> {
                    GroupDismountCMD.dismount(group, false);
                    sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Safely dismounted your selected group!", NamedTextColor.GREEN)));
                    if (hadAI){
                        ((LivingEntity) vehicle).setAI(true);
                    }
                    DisplayControllerManager.unregisterEntity(vehicle);
                }, 20);
            }
            return;
        }

        Entity vehicle = GroupRideCMD.getVehicle(sender, type);
        if (vehicle == null){
            return;
        }
        if (vehicle instanceof LivingEntity le){
            hadAI = le.hasAI();
        }
        else{
            hadAI = false;
        }

        DisplayAPI.getScheduler().runLater(() -> {
            for (SpawnedDisplayEntityGroup g : DisplayUtils.getGroupPassengers(vehicle)){
                GroupDismountCMD.dismount(g, false);
            }
            if (hadAI){
                ((LivingEntity) vehicle).setAI(true);
            }
        }, 20);


        DisplayControllerManager.unregisterEntity(vehicle);
        sender.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Dismounted all groups riding the entity!", NamedTextColor.GREEN)));
    }

}