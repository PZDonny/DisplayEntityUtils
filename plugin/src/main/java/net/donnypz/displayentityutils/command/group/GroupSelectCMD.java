package net.donnypz.displayentityutils.command.group;

import io.papermc.paper.entity.TeleportFlag;
import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.command.gizmo.GizmoCMD;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.version.folia.FoliaUtils;
import net.donnypz.displayentityutils.utils.version.folia.Scheduler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Set;

class GroupSelectCMD extends PlayerSubCommand {
    ClickCallback.Options clickOptions = ClickCallback.Options.builder()
            .uses(ClickCallback.UNLIMITED_USES)
            .lifetime(Duration.ofMinutes(5))
            .build();
    GroupSelectCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("select", parentSubCommand, Permission.GROUP_SELECT);
        setTabComplete(2, "<distance>");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!hasMinimumArguments(player, args)) return;

        try{
            double distance = Double.parseDouble(args[2]);
            if (distance <= 0){
                throw new IllegalArgumentException();
            }
            player.sendMessage(Component.empty());
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Finding groups within "+distance+" blocks...", NamedTextColor.YELLOW)));
            getSelectableGroups(player, distance);
        }
        catch(IllegalArgumentException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid distance! The distance must be a positive number.", NamedTextColor.RED)));
        }
    }

    private void getSelectableGroups(Player player, double distance){
        Set<SpawnedDisplayEntityGroup> groups = DisplayGroupManager.getOrCreateNearbySpawnedGroups(player.getLocation(), distance);
        if (groups.isEmpty()){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("No nearby groups found!", NamedTextColor.RED)));
            player.sendMessage(Component.text("| Move to a different location or increase your search distance.", NamedTextColor.GRAY, TextDecoration.ITALIC));
            player.sendMessage(Component.text("| Use \"/deu group selectpacket\" to mark packet-based groups in your current chunk.", NamedTextColor.GRAY, TextDecoration.ITALIC));
            return;
        }

        player.sendMessage(Component.text("| Groups found! Click to select.", NamedTextColor.GREEN));
        for (SpawnedDisplayEntityGroup group: groups){
            Component groupTag = MiniMessage.miniMessage().deserialize("- Tag: " + (group.hasTag() ? "<gray>" + group.getTag() : "<red>No Tag"));

            Component teleport = Component.text("[TELEPORT]", NamedTextColor.AQUA)
                    .clickEvent(ClickEvent.callback(audience -> {
                        Location groupLoc = group.getLocation();
                        if (groupLoc == null){
                            audience.sendMessage(Component.text("Group no longer spawned or is invalid.", NamedTextColor.RED));
                            return;
                        }
                        FoliaUtils.teleport((Player) audience, groupLoc, TeleportFlag.EntityState.RETAIN_PASSENGERS);
                    }, clickOptions));

            Component glow = Component.text("[GLOW]", NamedTextColor.YELLOW)
                    .clickEvent(ClickEvent.callback(audience -> {
                        if (!group.isSpawned()){
                            audience.sendMessage(Component.text("Group no longer spawned or is invalid.", NamedTextColor.RED));
                            return;
                        }
                        group.glowAndMarkInteractions((Player) audience, 40);
                    }, clickOptions));

            Component select = Component.text("[SELECT]", NamedTextColor.GREEN)
                    .clickEvent(ClickEvent.callback(audience -> {
                        GroupCMD.selectGroup((Player) audience, group, false, true);
                    }, clickOptions));

            Component groupMessage = groupTag
                    .appendSpace()
                    .append(select)
                    .appendSpace()
                    .append(glow)
                    .appendSpace()
                    .append(teleport);
            player.sendMessage(groupMessage);
        }
    }

    @Override
    protected String getDescription() {
        return "Select from nearby groups within the given distance";
    }
}