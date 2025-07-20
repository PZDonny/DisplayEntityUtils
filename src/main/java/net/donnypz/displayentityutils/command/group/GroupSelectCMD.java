package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.GroupResult;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

class GroupSelectCMD extends PlayerSubCommand {
    GroupSelectCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("selectnearest", parentSubCommand, Permission.GROUP_SELECT);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter a number for the distance to select interaction entities", NamedTextColor.RED)));
            player.sendMessage(Component.text("/mdis group selectnearest <interaction-distance>", NamedTextColor.GRAY));
            return;
        }

        try {
            double interactionDistance = Double.parseDouble(args[2]);
            GroupResult result = DisplayGroupManager.getSpawnedGroupNearLocation(player.getLocation(), 2.5f, player);
            if (result == null || result.group() == null){
                return;
            }
            SpawnedDisplayEntityGroup group = result.group();

            boolean selectResult = DisplayGroupManager.setSelectedSpawnedGroup(player, group);
            if (selectResult){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Successfully selected group!", NamedTextColor.GREEN)));
            }
            else{
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Failed to select group! Another player already has that group selected!", NamedTextColor.RED)));
                return;
            }

            group.getUnaddedInteractionEntitiesInRange(interactionDistance, true);
            int selectDuration = 50;
            group.glowAndOutline(player, selectDuration);
            new BukkitRunnable(){
                int maxIterations = selectDuration/2;
                int iteration = 0;
                @Override
                public void run() {
                    if (iteration == maxIterations){
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
            }.runTaskTimer(DisplayEntityPlugin.getInstance(), 0, 2);

            if (DEUCommandUtils.removeRelativePoints(player)){
                player.sendMessage(Component.text("Your previewed points have been despawned since you have changed your selected group", NamedTextColor.GRAY, TextDecoration.ITALIC));
            }
        } catch (NumberFormatException e) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter a number for the distance to select interaction entities", NamedTextColor.RED)));
        }
    }
}
