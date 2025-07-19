package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GroupInfoCMD extends PlayerSubCommand {
    GroupInfoCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("info", parentSubCommand, Permission.GROUP_INFO);
    }

    @Override
    public void execute(Player player, String[] args) {

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        player.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
        String groupTag = group.getTag();
        groupTag = groupTag == null ? "<red>NOT SET" : "<yellow>"+groupTag;

        player.sendMessage(MiniMessage.miniMessage().deserialize("Group Tag: <yellow>"+groupTag));
        player.sendMessage(MiniMessage.miniMessage().deserialize("World: <yellow>"+group.getWorldName()));
        player.sendMessage(MiniMessage.miniMessage().deserialize("Total Parts: <yellow>"+(group.getParts().size())));
        player.sendMessage(MiniMessage.miniMessage().deserialize("Is Persistent: <yellow>"+group.isPersistent()));
        player.sendMessage(MiniMessage.miniMessage().deserialize("Chunk Load Persistence Overriding: <yellow>"+group.allowsPersistenceOverriding()));

        Location loc = group.getLocation();
        player.sendMessage(MiniMessage.miniMessage().deserialize("Pitch & Yaw: <yellow>"+loc.getPitch()+", "+loc.getYaw()));
        player.sendMessage(MiniMessage.miniMessage().deserialize("Scale Multiplier: <yellow>"+group.getScaleMultiplier()));

        String animTag = group.getSpawnAnimationTag();
        animTag = animTag == null ? "<red>NOT SET" : "<yellow>"+animTag;

        DisplayAnimator.AnimationType type = group.getSpawnAnimationType();
        String animType = type == null ? "<red>NOT SET" : "<yellow>"+type.name();

        LoadMethod loadMethod = group.getSpawnAnimationLoadMethod();
        String animLoadMethod = loadMethod == null ? "<red>NOT SET" : "<yellow>"+loadMethod.name();

        player.sendMessage(MiniMessage.miniMessage().deserialize("Spawn Animation Tag: "+animTag));
        player.sendMessage(MiniMessage.miniMessage().deserialize("Spawn Animation Type: "+animType));
        player.sendMessage(MiniMessage.miniMessage().deserialize("Spawn Animation Storage: "+animLoadMethod));
        DEUCommandUtils.sendGlowColor(player, group.getGlowColor());
    }
}
