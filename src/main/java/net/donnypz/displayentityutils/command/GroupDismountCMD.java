package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.UUID;

class GroupDismountCMD implements ConsoleUsableSubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(sender, Permission.GROUP_TRANSFORM)){
            return;
        }

        if (args.length < 3){
            sender.sendMessage("Incorrect Usage! /mdis group dismount <keep | despawn> [-target | player-name | entity-uuid]");
            return;
        }
        if (args.length < 4) {
            if (!(sender instanceof Player player)){
                sender.sendMessage(Component.text("Incorrect Console Usage! /mdis group dismount <keep | despawn> <player-name | entity-uuid>", NamedTextColor.RED));
                return;
            }

            SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
            if (group == null) {
                DisplayEntityPluginCommand.noGroupSelection(player);
                return;
            }
            Entity vehicle = group.dismount();
            if (vehicle == null){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Your selected group is not riding an entity!", NamedTextColor.RED)));
            }
            else{
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Successfully dismounted your selected group!", NamedTextColor.GREEN)));
            }
            return;
        }

        String value = args[3];
        if (!(sender instanceof Player) && value.equalsIgnoreCase("-target")){
            sender.sendMessage(Component.text("You cannot use \"-target\" in console!", NamedTextColor.RED));
            return;
        }

        String retainOption = args[2];
        boolean keep;
        if (retainOption.equalsIgnoreCase("keep")){
            keep = true;
        }
        else if (retainOption.equalsIgnoreCase("despawn")){
            keep = false;
        }
        else{
            sender.sendMessage("Incorrect Usage! /mdis group dismount <keep | despawn> [-target | player-name | entity-uuid]");
            return;
        }


        Entity vehicle = GroupRideCMD.getVehicle(sender, value);
        if (vehicle == null){
            return;
        }


        for (SpawnedDisplayEntityGroup g : DisplayUtils.getGroupPassengers(vehicle)){
            g.dismount();
            g.stopFollowingEntity();
            if (!keep){
                g.unregister(true, true);
            }
        }

        sender.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Successfully dismounted all groups riding the entity!", NamedTextColor.GREEN)));
    }
}
