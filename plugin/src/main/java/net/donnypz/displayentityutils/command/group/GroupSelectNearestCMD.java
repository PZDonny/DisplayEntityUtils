package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.GroupResult;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

class GroupSelectNearestCMD extends PlayerSubCommand {
    GroupSelectNearestCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("selectnearest", parentSubCommand, Permission.GROUP_SELECT);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a number for the distance to search for the nearest group", NamedTextColor.RED)));
            player.sendMessage(Component.text("/mdis group selectnearest <distance>", NamedTextColor.GRAY));
            return;
        }

        try {
            double searchDistance = Double.parseDouble(args[2]);
            if (searchDistance <= 0 ) throw new NumberFormatException();
            GroupResult result = DisplayGroupManager.getSpawnedGroupNearLocation(player.getLocation(), searchDistance, player);
            if (result == null || result.group() == null){
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

            group.addMissingInteractionEntities(searchDistance);
            int selectDuration = 50;
            group.glowAndMarkInteractions(player, selectDuration);
            new BukkitRunnable(){
                int maxIterations = selectDuration/2;
                int iteration = 0;
                @Override
                public void run() {
                    if (iteration == maxIterations || !group.isSpawned()){
                        cancel();
                        return;
                    }
                    try{
                        Location groupLoc = group.getLocation();
                        player.spawnParticle(Particle.END_ROD, groupLoc, 1, 0,0,0,0.01);
                        iteration++;
                    }
                    catch(NullPointerException e){
                        cancel();
                    }
                }
            }.runTaskTimer(DisplayAPI.getPlugin(), 0, 2);

            if (RelativePointUtils.removeRelativePoints(player)){
                player.sendMessage(Component.text("Your previewed points have been despawned since you have changed your selected group", NamedTextColor.GRAY, TextDecoration.ITALIC));
            }
            GroupCMD.groupToPacketInfo(player);
        } catch (NumberFormatException e) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid distance! The distance must be a positive number.", NamedTextColor.RED)));
        }
    }
}
