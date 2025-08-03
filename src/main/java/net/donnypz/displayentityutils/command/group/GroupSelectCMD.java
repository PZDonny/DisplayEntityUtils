package net.donnypz.displayentityutils.command.group;

import io.papermc.paper.entity.TeleportFlag;
import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.GroupResult;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;

class GroupSelectCMD extends PlayerSubCommand {
    ClickCallback.Options clickOptions = ClickCallback.Options.builder()
            .uses(ClickCallback.UNLIMITED_USES)
            .lifetime(Duration.ofMinutes(5))
            .build();
    GroupSelectCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("select", parentSubCommand, Permission.GROUP_SELECT);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Enter a number for the distance to search for groups", NamedTextColor.RED)));
            player.sendMessage(Component.text("/mdis group select <distance>", NamedTextColor.GRAY));
            return;
        }

        try{
            double distance = Double.parseDouble(args[2]);
            if (distance <= 0){
                throw new IllegalArgumentException();
            }
            player.sendMessage(Component.empty());
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Finding groups within "+distance+" blocks...", NamedTextColor.YELLOW)));
            getSelectableGroups(player, distance);
        }
        catch(IllegalArgumentException e){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Invalid distance! The distance must be a positive number.", NamedTextColor.RED)));
        }
    }

    private void getSelectableGroups(Player player, double distance){
        List<GroupResult> groups = DisplayGroupManager.getSpawnedGroupsNearLocation(player.getLocation(), distance);
        if (groups.isEmpty()){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("No nearby groups found!", NamedTextColor.RED)));
            player.sendMessage(Component.text("| Move to a different location or increase your search distance.", NamedTextColor.GRAY, TextDecoration.ITALIC));
            return;
        }

        player.sendMessage(Component.text("| Groups found! Click to select.", NamedTextColor.GREEN));
        for (GroupResult result : groups){
            SpawnedDisplayEntityGroup g = result.group();
            Component groupTag = MiniMessage.miniMessage().deserialize("- Tag: " + (g.hasTag() ? "<gray>" + g.getTag() : "<red>No Tag"));
            Component teleport = Component.text("[TELEPORT]", NamedTextColor.AQUA)
                    .clickEvent(ClickEvent.callback(audience -> {
                        Location groupLoc = g.getLocation();
                        if (groupLoc == null){
                            audience.sendMessage(Component.text("Group no longer spawned or is invalid.", NamedTextColor.RED));
                            return;
                        }
                        ((Player) audience).teleport(groupLoc, TeleportFlag.EntityState.RETAIN_PASSENGERS);
                    }, clickOptions));
            Component glow = Component.text("[GLOW]", NamedTextColor.YELLOW)
                    .clickEvent(ClickEvent.callback(audience -> {
                        if (!g.isSpawned()){
                            audience.sendMessage(Component.text("Group no longer spawned or is invalid.", NamedTextColor.RED));
                            return;
                        }
                        g.glow((Player) audience, 60);
                    }, clickOptions));
            Component select = Component.text("[SELECT]", NamedTextColor.GREEN)
                    .clickEvent(ClickEvent.callback(audience -> {
                        Player p = (Player) audience;
                        if (!g.isSpawned()){
                            audience.sendMessage(Component.text("Group no longer spawned or is invalid.", NamedTextColor.RED));
                            return;
                        }
                        boolean selectResult = DisplayGroupManager.setSelectedSpawnedGroup(p, g);
                        if (selectResult){
                            g.addMissingInteractionEntities(distance);
                            p.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Successfully selected group!", NamedTextColor.GREEN)));
                            if (DEUCommandUtils.removeRelativePoints(p)){
                                p.sendMessage(Component.text("Your previewed points have been despawned since you have changed your selected group", NamedTextColor.GRAY, TextDecoration.ITALIC));
                            }
                        }
                        else{
                            p.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Failed to select group! Another player already has that group selected!", NamedTextColor.RED)));
                            return;
                        }
                        int selectDuration = 50;
                        g.glowAndOutline(p, selectDuration);
                        new BukkitRunnable(){
                            int maxIterations = selectDuration/2;
                            int iteration = 0;
                            @Override
                            public void run() {
                                if (iteration == maxIterations || !g.isSpawned()){
                                    cancel();
                                    return;
                                }
                                try{
                                    Location groupLoc = g.getLocation();
                                    p.spawnParticle(Particle.END_ROD, groupLoc, 1, 0,0,0,0.01);
                                    iteration++;
                                }
                                catch(NullPointerException e){
                                    cancel();
                                }
                            }
                        }.runTaskTimer(DisplayEntityPlugin.getInstance(), 0, 2);

                    }, clickOptions));
            Component groupMessage = groupTag
                    .appendSpace()
                    .append(select)
                    .appendSpace()
                    .append(glow)
                    .appendSpace()
                    .append(teleport);
            player.sendMessage(groupMessage);
            GroupCMD.groupToPacketInfo(player);
        }
    }
}
