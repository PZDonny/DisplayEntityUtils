package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.GroupResult;
import net.donnypz.displayentityutils.utils.version.folia.Scheduler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

class GroupSelectNearestCMD extends PlayerSubCommand {
    GroupSelectNearestCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("selectnearest", parentSubCommand, Permission.GROUP_SELECT);
        setTabComplete(2, "<distance>");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a number for the distance to search for the nearest group", NamedTextColor.RED)));
            player.sendMessage(Component.text("/deu group selectnearest <distance>", NamedTextColor.GRAY));
            return;
        }

        try {
            double searchDistance = Double.parseDouble(args[2]);
            if (searchDistance <= 0 ) throw new NumberFormatException();
            GroupResult result = DisplayGroupManager.getSpawnedGroupNearLocation(player.getLocation(), searchDistance);
            if (result == null || result.group() == null){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You are not near any spawned display entity groups!", NamedTextColor.RED)));
                player.sendMessage(Component.text("| Use \"/deu group markpacketgroups\" to mark packet-based groups in your current chunk.", NamedTextColor.GRAY, TextDecoration.ITALIC));
                return;
            }
            SpawnedDisplayEntityGroup group = result.group();

            boolean selectResult = DisplayGroupManager.setSelectedGroup(player, group);
            if (selectResult){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Selected the nearest group!", NamedTextColor.GREEN)));
            }
            else{
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Failed to select nearest group! Another player already has that group selected!", NamedTextColor.RED)));
                return;
            }

            group.addMissingEntities(searchDistance);
            int selectDuration = 50;
            group.glowAndMarkInteractions(player, selectDuration);
            Entity entity = group.getMasterPart().getEntity();

            final int maxIterations = selectDuration/2;
            AtomicInteger iteration = new AtomicInteger();
            DisplayAPI.getScheduler().entityRunTimer(entity, new Scheduler.SchedulerRunnable(){

                @Override
                public void run() {
                    if (iteration.get() == maxIterations || !group.isSpawned()){
                        cancel();
                        return;
                    }
                    try{
                        Location groupLoc = group.getLocation();
                        player.spawnParticle(Particle.END_ROD, groupLoc, 1, 0,0,0,0.01);
                        iteration.set(iteration.get()+1);
                    }
                    catch(NullPointerException e){
                        cancel();
                    }
                }
            }, 0, 2);

            DisplayEntityPluginCommand.hideRelativePoints(player);
            GroupCMD.groupToPacketInfo(player);
        } catch (NumberFormatException e) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid distance! The distance must be a positive number.", NamedTextColor.RED)));
        }
    }
}
